package com.pvr.gles.bitmap;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.pvr.gles.utils.LogUtils;

public class BitmapGLSurfaceView extends GLSurfaceView {

    private Context mContext;

    public BitmapGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public BitmapGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LogUtils.i("init");
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(new BitmapRenderer(context));
    }

}
