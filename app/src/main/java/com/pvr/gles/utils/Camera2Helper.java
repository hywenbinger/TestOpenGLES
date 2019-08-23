package com.pvr.gles.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.text.TextUtils;
import android.view.Surface;
import java.util.Arrays;

public class Camera2Helper {

    private Context mContext;
    private SurfaceTexture mSurfaceTexture;
    private CameraManager mCameraManager;
    private String mCameraId;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mBuilder;
    private CameraCaptureSession mCameraCaptureSession;
    private CaptureRequest mCaptureRequest;

    public Camera2Helper(Context context) {
        mContext = context;
    }

    public void startCamera(SurfaceTexture surfaceTexture) {
        this.mSurfaceTexture = surfaceTexture;
        startCamera();
    }

    public void stopPrive() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void startCamera(){
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIds = mCameraManager.getCameraIdList();
            if (cameraIds == null) {
                return;
            }
            if (cameraIds.length == 0) {
                return;
            }
            mCameraId = cameraIds[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        openCamera();
    }

    public void openCamera() {
        if (TextUtils.isEmpty(mCameraId)) {
            return;
        }
        if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            mCameraManager.openCamera(mCameraId, mCameraStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback mCameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            if(mCameraDevice != null){
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            if(mCameraDevice != null){
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
    };

    private void createCameraPreviewSession(){
        try {
            mBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);//设置自动对焦
            Surface surface = new Surface(mSurfaceTexture);
            if(surface != null){
                mBuilder.addTarget(surface);
                mCameraDevice.createCaptureSession(Arrays.asList(surface), mSessionStateCallback, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            mCameraCaptureSession = session;
            mCaptureRequest = mBuilder.build();
            try {
                mCameraCaptureSession.setRepeatingRequest(mCaptureRequest,null,null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };

}
