package com.pvr.gles.filter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;

import com.pvr.gles.R;

import java.io.File;
import java.io.IOException;

public class FilterActivity extends AppCompatActivity {

    private FilterGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        mGLSurfaceView = findViewById(R.id.filterGLSurfaceView);
    }

    public void click(View view) {
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setSurface(new Surface(mGLSurfaceView.getSurfaceTexture()));
        try {
            File file = new File("/sdcard/test.mp4");
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(Uri.fromFile(file).toString());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            	mediaPlayer.start();
            }
        });
    }
}
