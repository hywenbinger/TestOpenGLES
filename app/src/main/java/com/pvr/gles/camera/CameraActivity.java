package com.pvr.gles.camera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pvr.gles.R;
import com.pvr.gles.utils.Camera2Helper;

public class CameraActivity extends AppCompatActivity {

    private Camera2Helper mCamera2Helper;
    private CameraGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mGLSurfaceView = findViewById(R.id.cameraGLSurfaceView);
        mCamera2Helper = new Camera2Helper(this);
    }

    public void click(View view) {
        mCamera2Helper.startCamera(mGLSurfaceView.getSurfaceTexture());
    }
}
