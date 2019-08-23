package com.pvr.gles.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.pvr.gles.utils.LogUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener{

    private Context mContext;
    private SurfaceTexture mSurfaceTexture;
    private int mTextureID = -1;
    private boolean mIsUpdate;
    private float[] mTransformMatrix = new float[16];
    private CameraDrawer mCameraDrawer;

    public CameraGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LogUtils.i("init");
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public SurfaceTexture getSurfaceTexture(){
        return mSurfaceTexture;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mIsUpdate = true;
        requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.i("onSurfaceCreated");
        mTextureID = createTextureID();
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setDefaultBufferSize(1280, 960);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCameraDrawer = new CameraDrawer(mTextureID);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.i("onSurfaceChanged--->"+width+", "+height);
        //宽高
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mIsUpdate) {
            LogUtils.i("onDrawFrame");
            //清空颜色
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            //设置背景颜色
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            //更新接收到的图像数据到其绑定的OpenGL纹理中
            //会绑定到GL_TEXTURE_EXTERNAL_OES纹理目标对象中
            mSurfaceTexture.updateTexImage();
            //获取图像数据流的坐标变换矩阵
            mSurfaceTexture.getTransformMatrix(mTransformMatrix);
            //真正的图像处理
            mCameraDrawer.draw(mTransformMatrix);
            mIsUpdate = false;
        }
    }

    private int createTextureID() {
        int textures[] = new int[1];
        GLES20.glGenTextures(textures.length, textures, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        return textures[0];
    }

}
