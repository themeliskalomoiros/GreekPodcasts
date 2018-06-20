package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.concurrent.TimeUnit;

import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;

import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
import static gr.kalymnos.sk3m3l10.greekpodcasts.media_playing_service.PlaybackInfoListener.State.*;
import static gr.kalymnos.sk3m3l10.greekpodcasts.media_playing_service.PlayerHolder.DURATION_KEY;
import static gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode.EPISODE_KEY;
import static gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode.INDEX_KEY;
import static gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode.TITLE_KEY;
import static gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast.POSTER_KEY;

public class PlaybackUtils {

    private PlaybackUtils() {
    }

    public static PlaybackStateCompat playbackState(long actions, int state, long currentPosition, float speed,
                                                    PlaybackStateCompat.Builder builder) {
        return builder.setActions(actions)
                .setState(state, currentPosition, speed)
                .build();
    }

    public static MediaMetadataCompat mediaMetadata(String title, String posterUrl, long duration,
                                                    int trackIndex, String trackId, MediaMetadataCompat.Builder builder) {
        return builder.putString(TITLE_KEY, title)
                .putString(POSTER_KEY, posterUrl)
                .putLong(DURATION_KEY, duration)
                .putLong(INDEX_KEY,trackIndex)
                .putString(Episode.FIREBASE_ID_KEY,trackId)
                .build();
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
        return playerState != IDLE || playerState != INITIALIZED || playerState != STOPPED
                || playerState != ERROR;
    }
}
