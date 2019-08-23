package com.pvr.gles.fbo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pvr.gles.R;

/**
 * 我们需要对纹理进行多次渲染采样时，而这些渲染采样是不需要展示给用户看的，
 * 所以我们就可以用一个单独的缓冲对象（离屏渲染）来存储我们的这几次渲染采样的结果，等处理完后才显示到窗口上
 */
public class BitmapFBOActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_fbo);
    }
}
