package com.pvr.gles.fbo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.pvr.gles.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BitmapFBORenderer implements GLSurfaceView.Renderer {

    private BitmapFBOTexture bitmapFboTexture;
    private BitmapRenderTexture bitmapRenderTexture;

    public BitmapFBORenderer(Context context) {
        bitmapFboTexture = new BitmapFBOTexture(context);
        bitmapFboTexture.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.bg));

        bitmapRenderTexture = new BitmapRenderTexture(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        bitmapFboTexture.onSurfaceCreated();
        bitmapRenderTexture.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //宽高
        GLES20.glViewport(0, 0, width, height);

        bitmapFboTexture.onSurfaceChanged(width, height);
        bitmapRenderTexture.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //设置背景颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //FBO处理
        bitmapFboTexture.draw();
        //通过FBO处理之后，拿到纹理id，然后渲染
        bitmapRenderTexture.draw(bitmapFboTexture.getFboTextureId());
    }
}