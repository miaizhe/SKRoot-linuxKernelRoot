package com.linux.permissionmanager.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

public class BackgroundMusicManager {
    private static BackgroundMusicManager instance;
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private Context context;
    private Uri currentUri;
    private OnStateChangedListener stateChangedListener;

    public interface OnStateChangedListener {
        void onStateChanged(boolean isPlaying);
    }

    private BackgroundMusicManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized BackgroundMusicManager getInstance(Context context) {
        if (instance == null) {
            instance = new BackgroundMusicManager(context);
        }
        return instance;
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.stateChangedListener = listener;
    }

    public void play(Uri uri) {
        if (uri == null) return;
        
        // If same URI and is playing/paused
        if (uri.equals(currentUri) && mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPaused = false;
                notifyStateChanged(true);
            }
            return;
        }

        stop();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentUri = uri;
            isPaused = false;
            notifyStateChanged(true);
        } catch (Exception e) {
            Log.e("BackgroundMusicManager", "Failed to play music", e);
            // Don't clear currentUri immediately to allow retries or UI consistency if needed,
            // but effectively we failed.
            notifyStateChanged(false);
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
            notifyStateChanged(false);
        }
    }

    public void resume() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.start();
            isPaused = false;
            notifyStateChanged(true);
        } else if (mediaPlayer == null && currentUri != null) {
            play(currentUri);
        }
    }
    
    public void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPaused = false;
        notifyStateChanged(false);
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
    
    public Uri getCurrentUri() {
        return currentUri;
    }

    private void notifyStateChanged(boolean isPlaying) {
        if (stateChangedListener != null) {
            stateChangedListener.onStateChanged(isPlaying);
        }
    }
}
