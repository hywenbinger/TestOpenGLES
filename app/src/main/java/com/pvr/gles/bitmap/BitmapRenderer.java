package com.pvr.gles.bitmap;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.pvr.gles.utils.LogUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BitmapRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private BitmapTexture bitmapTexture;

    public BitmapRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.i("onSurfaceCreated");
        bitmapTexture = new BitmapTexture(context);
        bitmapTexture.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.i("onSurfaceChanged--->"+width+", "+height);
        //宽高
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        LogUtils.i("onDrawFrame");
        //清空颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //设置背景颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        bitmapTexture.draw();
    }

}
