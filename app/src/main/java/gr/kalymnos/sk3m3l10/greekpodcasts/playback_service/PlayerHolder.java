package gr.kalymnos.sk3m3l10.greekpodcasts.playback_service;

/*  This class will hold candidate players that will play the podcast files.
 *  Taken from: https://medium.com/google-developers/building-a-simple-audio-app-in-android-part-1-3-c14d1a66e0f1   */

import android.content.Context;

interface PlayerHolder {

    static final int DURATION_STREAMING = -1;
    String DURATION_KEY = "duration_key";

    void release();

    void setPlaybackInfoListener(PlaybackInfoListener listener);

    void play();

    void pause();

    void stop();

    void reset();

    void loadUrl(String url);

    void seekTo(int position);

    int getCurrentPosition();

    int getDuration();

    boolean isPlaying();

    void setContext(Context context);
}