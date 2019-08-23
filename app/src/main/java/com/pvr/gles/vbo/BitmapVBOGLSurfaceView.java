package com.pvr.gles.vbo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.pvr.gles.utils.LogUtils;

public class BitmapVBOGLSurfaceView extends GLSurfaceView {

    private Context mContext;

    public BitmapVBOGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public BitmapVBOGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LogUtils.i("init");
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(new BitmapVBORenderer(context));
    }

}

