package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.content.Context;
import android.media.AudioManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.concurrent.TimeUnit;

import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.*;

public class PlaybackUtils {

    private PlaybackUtils() {
    }

    public static PlaybackStateCompat getPlaybackStateFromPlayerState(int playerState, long currentPosition,
                                                                      float playbackSpeed, PlaybackStateCompat.Builder stateBuilder) {
        switch (playerState) {
            case IDLE:
                return stateBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 0)
                        .build();
            case INITIALIZED:
                return stateBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 0)
                        .setActions(PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PREPARE)
                        .build();
            case PREPARED:
                return stateBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 0)
                        .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_SEEK_TO)
                        .build();
            case STARTED:
                return stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, currentPosition, playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE
                                | PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_SEEK_TO
                                | PlaybackStateCompat.ACTION_PLAY)
                        .build();
            case PAUSED:
                return stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, currentPosition, playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE
                                | PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_SEEK_TO
                                | PlaybackStateCompat.ACTION_PAUSE)
                        .build();
            case STOPPED:
                return stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, currentPosition, playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PREPARE
                                | PlaybackStateCompat.ACTION_STOP)
                        .build();
            case COMPLETED:
                return stateBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_SEEK_TO
                                | PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_PAUSE
                                | PlaybackStateCompat.ACTION_PLAY_PAUSE)
                        .build();
            case ERROR:
                return stateBuilder.setState(PlaybackStateCompat.STATE_ERROR, 0, 0)
                        .build();
            case END:
                return null;
            default:
                throw new IllegalArgumentException(PlaybackUtils.class.getSimpleName() + ": Unknown player state");
        }
    }

    private static int getAudioFocusResult(Context context, AudioManager.OnAudioFocusChangeListener listener) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus for playback, this registers the afChangeListener
        return am.requestAudioFocus(listener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
    }

    public static void abandonAudioFocus(Context context, AudioManager.OnAudioFocusChangeListener listener) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(listener);
    }

    public static boolean gainedAudioFocus(Context context, AudioManager.OnAudioFocusChangeListener listener) {
        return getAudioFocusResult(context
                , listener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public static String playbackPositionString(long positionInMilli) {
        long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(positionInMilli);
        int minutes = 0;
        int seconds = 0;

        while (true) {
            durationSeconds -= 60;
            if (durationSeconds >= 0) {
                ++minutes;
            } else {
                seconds = (int) durationSeconds + 60;
                break;
            }
        }

        String stringMinutes = minutes < 10 ? "0" + minutes : "" + minutes;
        String stringSeconds = seconds < 10 ? "0" + seconds : "" + seconds;

        return String.format("%s:%s", stringMinutes, stringSeconds);
    }

    public static boolean validStateToPrepare(int playerState) {
        return playerState == INITIALIZED || playerState == STOPPED;
    }

    public static boolean validStateToPlay(int playerState) {
        return playerState == PREPARED || playerState == STARTED || playerState == PAUSED
                || playerState == COMPLETED;
    }

    public static boolean validStateToGetPosition(int playerState) {
        boolean b = playerState == IDLE || playerState == INITIALIZED || playerState == PREPARED || playerState == STARTED ||
                playerState == COMPLETED || playerState == STOPPED || playerState == PAUSED;
        return b;
    }

    public static boolean validStateToGetDuration(int playerState) {
        boolean b = playerState == PREPARED || playerState == STARTED || playerState == PAUSED
                || playerState == STOPPED || playerState == COMPLETED;
        return b;
    }

    public static boolean validStateToPause(int playerState) {
        return playerState == STARTED || playerState == PAUSED || playerState == COMPLETED;
    }

    public static boolean validStateToSeek(int playerState) {
        return playerState == PREPARED || playerState == STARTED || playerState == PAUSED
                || playerState == COMPLETED;
    }

    public static boolean validStateToSetDataSource(int playerState) {
        return playerState == IDLE;
    }

    public static boolean validStateToStop(int playerState){
        return playerState == PREPARED || playerState == STARTED || playerState == PAUSED
                || playerState == STOPPED || playerState == COMPLETED;
    }
}
