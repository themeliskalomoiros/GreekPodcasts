package gr.kalymnos.sk3m3l10.greekpodcasts.media_playing_service;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface PlaybackInfoListener {

    @IntDef({State.IDLE, State.INITIALIZED, State.PREPARED, State.STARTED, State.COMPLETED,
            State.STOPPED, State.ERROR, State.END})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {

        int IDLE = -1;
        int INITIALIZED = 0;
        int PREPARED = 1;
        int STARTED = 2;
        int PAUSED = 3;
        int COMPLETED = 4;
        int STOPPED = 5;
        int ERROR = 6;
        int END = 7;
    }

    public static String convertStateToString(@State int state) {
        String stateString;
        switch (state) {
            case State.COMPLETED:
                stateString = "COMPLETED";
                break;

            case State.PAUSED:
                stateString = "PAUSED";
                break;
            case State.IDLE:
                stateString = "IDLE";
                break;
            case State.INITIALIZED:
                stateString = "INITIALIZED";
                break;
            case State.PREPARED:
                stateString = "PREPARED";
                break;
            case State.STARTED:
                stateString = "STARTED";
                break;
            case State.STOPPED:
                stateString = "STOPPED";
                break;
            case State.ERROR:
                stateString = "ERROR";
                break;
            case State.END:
                stateString = "END";
                break;
            default:
                stateString = "N/A";
        }
        return stateString;
    }

    void onDurationChanged(int duration); 
    

    void onPositionChanged(int position); 
    

    void onStateChanged(@State int state); 
    

    void onPlaybackPrepared(); 
        
    

    void onPlaybackCompleted();

    void onErrorHappened(String errorMessage);
    
}
