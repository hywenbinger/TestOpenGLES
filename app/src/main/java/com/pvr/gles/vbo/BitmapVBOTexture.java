package com.pvr.gles.vbo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.pvr.gles.R;
import com.pvr.gles.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 使用顶点缓冲VBO
 *
 * 不使用VBO时，我们每次绘制（glDrawArrays）图形时都是从本地内存处获取顶点数据然后传输给OpenGL来绘制，这样就会频繁的操作CPU->GPU增大开销，从而降低效率。
 * 使用VBO，我们就能把顶点数据缓存到GPU开辟的一段内存中，然后使用时不必再从本地获取，而是直接从显存中获取，这样就能提升绘制的效率。
 *
 */
public class BitmapVBOTexture {

    //顶点坐标
    static float vertexData[] = {   // in counterclockwise order:
            -1f, -1f, 0.0f, // bottom left
            1f, -1f, 0.0f, // bottom right
            -1f, 1f, 0.0f, // top left
            1f, 1f, 0.0f,  // top right
    };

    //纹理坐标  对应顶点坐标  与之映射
    static float textureData[] = {   // in counterclockwise order:
            0f, 1f, 0.0f, // bottom left
            1f, 1f, 0.0f, // bottom right
            0f, 0f, 0.0f, // top left
            1f, 0f, 0.0f,  // top right
    };

    //每一次取点的时候取几个点
    static final int COORDS_PER_VERTEX = 3;

    //顶点个数
    private final int vertexCount = vertexData.length / COORDS_PER_VERTEX;

    //每一次取的总的点大小
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点4字节，共12字节


    private Context context;

    //位置
    private FloatBuffer vertexBuffer;
    //纹理
    private FloatBuffer textureBuffer;
    private int program;
    private int avPosition;
    //纹理位置
    private int afPosition;
    //纹理id
    private int textureId;
    //vbo id
    private int vboId;


    public BitmapVBOTexture(Context context) {
        this.context = context;

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4) // 分配字节缓冲区，保存顶点坐标
                .order(ByteOrder.nativeOrder()) // 设置顺序(本地顺序)
                .asFloatBuffer()
                .put(vertexData); // 放置顶点坐标数
        vertexBuffer.position(0); // 定位指针的位置，从该位置开始读取顶点数据

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }


    public void onSurfaceCreated() {
        String vertexSource = ShaderUtils.readRawTxt(context, R.raw.vertex_shader);
        String fragmentSource = ShaderUtils.readRawTxt(context, R.raw.fragment_shader);
        program = ShaderUtils.createProgram(vertexSource, fragmentSource);

        if (program > 0) {
            //获取顶点坐标字段
            avPosition = GLES20.glGetAttribLocation(program, "av_Position");
            //获取纹理坐标字段
            afPosition = GLES20.glGetAttribLocation(program, "af_Position");

            //创建vbo
            createVBO();

            int[] textureIds = new int[1];
            //创建纹理
            GLES20.glGenTextures(1, textureIds, 0);
            if (textureIds[0] == 0) {
                return;
            }
            textureId = textureIds[0];
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bg);

            if (bitmap == null) {
                return;
            }
            //设置纹理为2d图片
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }
    }

    public void draw() {
        //使用程序
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(avPosition);
        GLES20.glEnableVertexAttribArray(afPosition);

        //使用vbo设置
        useVboDraw();

        //直接设置
//        //设置顶点位置值
//        GLES20.glVertexAttribPointer(avPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
//        //设置纹理位置值
//        GLES20.glVertexAttribPointer(afPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureBuffer);

        //绘制 GLES20.GL_TRIANGLE_STRIP:复用坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(avPosition);
        GLES20.glDisableVertexAttribArray(afPosition);
    }


    private void createVBO() {
        //1. 创建VBO
        int[] vbos = new int[1];
        GLES20.glGenBuffers(vbos.length, vbos, 0);
        vboId = vbos[0];
        //2. 绑定VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        //3. 分配VBO需要的缓存大小
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + textureData.length * 4, null, GLES20.GL_STATIC_DRAW);
        //4. 为VBO设置顶点数据的值
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, textureData.length * 4, textureBuffer);
        //5. 解绑VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private void useVboDraw() {
        //1. 绑定VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        //2. 设置顶点数据
        GLES20.glVertexAttribPointer(avPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, 0);
        GLES20.glVertexAttribPointer(afPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexData.length * 4);
        //3. 解绑VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

}
