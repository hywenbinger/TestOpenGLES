package com.pvr.gles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pvr.gles.bitmap.BitmapActivity;
import com.pvr.gles.camera.CameraActivity;
import com.pvr.gles.filter.FilterActivity;
import com.pvr.gles.fbo.BitmapFBOActivity;
import com.pvr.gles.first.FirstActivity;
import com.pvr.gles.vbo.BitmapVBOActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBitmap(View view) {
        startActivity(new Intent(this, BitmapActivity.class));
    }

    public void onBitmapVBO(View view) {
        startActivity(new Intent(this, BitmapVBOActivity.class));
    }

    public void onBitmapFBO(View view) {
        startActivity(new Intent(this, BitmapFBOActivity.class));
    }

    public void onCamera(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void filter(View view) {
        startActivity(new Intent(this, FilterActivity.class));
    }

    public void first(View view) {
        startActivity(new Intent(this, FirstActivity.class));
    }
}
