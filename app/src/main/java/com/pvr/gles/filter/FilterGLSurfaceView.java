package com.pvr.gles.filter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import com.pvr.gles.utils.LogUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FilterGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private Context mContext;
    private SurfaceTexture mSurfaceTexture;
    private FilterDrawerGroup mCamera2DrawerGroup;
    private int mCameraTextureID = -1;
    private float[] mTransformMatrix;
    private long mTimestamp;
    private boolean mUpdateSurface;

    public FilterGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public FilterGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        LogUtils.i("onFrameAvailable");
        mUpdateSurface = true;
        this.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.i("onSurfaceCreated");
        mTransformMatrix = new float[16];
        mCameraTextureID = createTextureID();
        mSurfaceTexture = new SurfaceTexture(mCameraTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCamera2DrawerGroup = new FilterDrawerGroup(mCameraTextureID);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.i("onSurfaceChanged, width=" + width +", height="+height);
        mCamera2DrawerGroup.surfaceChangedSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mUpdateSurface) {
//            LogUtils.i("onDrawFrame");
            mSurfaceTexture.updateTexImage();
            mTimestamp = mSurfaceTexture.getTimestamp();
            mSurfaceTexture.getTransformMatrix(mTransformMatrix);
            mCamera2DrawerGroup.draw(mTimestamp, mTransformMatrix);
            mUpdateSurface = false;
        }
    }

    private int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

}
