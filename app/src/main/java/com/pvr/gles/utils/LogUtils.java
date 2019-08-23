package com.pvr.gles.utils;

import android.util.Log;

public class LogUtils {

    private static final String TAG = "TestOpenGLES";

    public static void i(String msg){
        Log.i(TAG, msg);
    }

    public static void e(String msg){
        Log.e(TAG, msg);
    }

}
