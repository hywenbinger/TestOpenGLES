package com.pvr.gles.fbo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.pvr.gles.utils.LogUtils;

public class BitmapFBOGLSurfaceView extends GLSurfaceView {

    private Context mContext;

    public BitmapFBOGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public BitmapFBOGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LogUtils.i("init");
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(new BitmapFBORenderer(context));
    }

}
