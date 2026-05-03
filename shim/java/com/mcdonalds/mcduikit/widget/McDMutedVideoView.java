package com.mcdonalds.mcduikit.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class McDMutedVideoView extends FrameLayout {
    private boolean mCanAdjustViewBounds;
    private boolean mPlaying;
    private int mPositionMs;
    private String mVideoPath;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private MediaPlayer.OnErrorListener mErrorListener;
    private MediaPlayer.OnInfoListener mInfoListener;
    private MediaPlayer.OnPreparedListener mPreparedListener;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;

    public McDMutedVideoView(Context context) {
        super(context);
    }

    public McDMutedVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public McDMutedVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public McDMutedVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCanAdjustViewBounds(boolean canAdjustViewBounds) {
        mCanAdjustViewBounds = canAdjustViewBounds;
    }

    public boolean getCanAdjustViewBounds() {
        return mCanAdjustViewBounds;
    }

    public void setVideoPath(String path) {
        mVideoPath = path;
    }

    public void setVideoURI(Uri uri) {
        mVideoPath = uri != null ? uri.toString() : null;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public void start() {
        mPlaying = true;
    }

    public void pause() {
        mPlaying = false;
    }

    public void suspend() {
        mPlaying = false;
    }

    public void resume() {
        mPlaying = true;
    }

    public void stopPlayback() {
        mPlaying = false;
        mPositionMs = 0;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public void seekTo(int msec) {
        mPositionMs = msec;
    }

    public int getCurrentPosition() {
        return mPositionMs;
    }

    public int getDuration() {
        return 0;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mCompletionListener = listener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        mErrorListener = listener;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener listener) {
        mInfoListener = listener;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        mPreparedListener = listener;
    }

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
        mBufferingUpdateListener = listener;
    }
}
