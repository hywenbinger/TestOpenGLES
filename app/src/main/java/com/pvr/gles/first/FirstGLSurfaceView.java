package com.pvr.gles.first;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FirstGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    //顶点着色器
    //gl_Position 和 gl_PointSize 是着色器中的特殊全局变量，接收输入。
    //a_Position 是自定义的vec4类型的变量。
    //attribute 只能存在于顶点着色器中，一般用于保存顶点数据，可以在数据缓冲区中读取数据。
    //数据缓存区中的顶点坐标会赋值给 a_Position ，a_Position 会传递给 gl_Position。
    //gl_PointSize 则固定了点的大小为 30。
    //有了顶点着色器，就能够为每个顶点生成最终的位置。
    private final String vertexShaderCode =
            "attribute vec4 a_Position;\n" +
                    "void main()\n" +
                    "{\n" +
                    "    gl_Position = a_Position;\n" +
                    "    gl_PointSize = 30.0;\n" +
                    "}";
    //片段着色器
    //片段着色器的主要目的就是告诉 GPU 每个片段的最终颜色应该是什么。
    //gl_FragColor 变量就是 OpenGL 最终渲染出来的颜色的全局变量。
    //u_Color 就是我们定义的变量，通过在 Java 层绑定到 u_Color变量并给它赋值，就会传递到 Native 层的gl_FragColor中。
    //mediump 指的就是片段着色器的精度，有三种可选，这里用中等精度就行了。
    //uniform 则表示该变量是不可变的了，也就是固定颜色了，目前显示固定颜色就好了。
    private final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform vec4 u_Color;\n" +
                    "void main()\n" +
                    "{\n" +
                    "    gl_FragColor = u_Color;\n" +
                    "}";

    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private int aColorLocation;
    private int aPositionLocation;

    private int program;

    private float[] pointVertex = {
            0f, 0f,
    };
    private FloatBuffer pointBuffer;

    public FirstGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public FirstGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);

        program = buildProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(program);

        pointBuffer = ByteBuffer
                .allocateDirect(pointVertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(pointVertex);
        pointBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        aColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        pointBuffer.position(0);
        //默认情况下，出于性能考虑，所有顶点着色器的属性（Attribute）变量都是关闭的，
        //意味着数据在着色器端是不可见的，哪怕数据已经上传到GPU，
        //由glEnableVertexAttribArray启用指定属性，才可在顶点着色器中访问逐顶点的属性数据。
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 2*4, pointBuffer);
        pointBuffer.position(0);

        //给片段着色器的 u_Color 赋值
        GLES20.glUniform4f(aColorLocation, 1.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, pointVertex.length / 2);
    }

    // 根据类型编译着色器
    private int compileShader(int type, String shaderCode) {
        // 根据不同的类型创建着色器 ID
        final int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId == 0) {
            return 0;
        }
        // 将着色器 ID 和着色器程序内容连接
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        // 编译着色器
        GLES20.glCompileShader(shaderObjectId);
        // 为验证编译结果是否失败
        final int[] compileStatus = new int[1];
        // glGetShaderiv函数比较通用，在着色器阶段和 OpenGL 程序阶段都会通过它来验证结果。
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            // 失败则删除
            GLES20.glDeleteShader(shaderObjectId);
            return 0;
        }
        return shaderObjectId;
    }

    // 创建 OpenGL 程序和着色器链接
    public int linkProgram(int vertexShaderId, int fragmentShaderId) {
        // 创建 OpenGL 程序 ID
        final int programObjectId = GLES20.glCreateProgram();
        if (programObjectId == 0) {
            return 0;
        }
        // 链接上 顶点着色器
        GLES20.glAttachShader(programObjectId, vertexShaderId);
        // 链接上 片段着色器
        GLES20.glAttachShader(programObjectId, fragmentShaderId);
        // 链接着色器之后，链接 OpenGL 程序
        GLES20.glLinkProgram(programObjectId);
        // 验证链接结果是否失败
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            // 失败则删除 OpenGL 程序
            GLES20.glDeleteProgram(programObjectId);
            return 0;
        }
        return programObjectId;
    }

    // 链接了 OpenGL 程序后，就是验证 OpenGL 是否可用。
    public boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        return validateStatus[0] != 0;
    }

    // 创建 OpenGL 程序过程
    public int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        // 编译顶点着色器
        int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource);
        // 编译片段着色器
        int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);
        int program = linkProgram(vertexShader, fragmentShader);
        validateProgram(program);
        return program;
    }

}
