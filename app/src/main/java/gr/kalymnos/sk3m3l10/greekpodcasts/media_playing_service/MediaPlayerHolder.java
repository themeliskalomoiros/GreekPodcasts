package gr.kalymnos.sk3m3l10.greekpodcasts.media_playing_service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

import static gr.kalymnos.sk3m3l10.greekpodcasts.media_playing_service.PlaybackInfoListener.State.*;

public class MediaPlayerHolder implements PlayerHolder {

    //  TODO:   If you want implement OnBufferedListener to gain control of buffered status.

    private static MediaPlayerHolder instance = null;

    private static MediaPlayer mediaPlayer;
    private static MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            reportState(PREPARED);
            if (mainPlayerListener != null) {
                mainPlayerListener.onPlaybackReady();
            }
            if (playbackInfoListener != null) {
                playbackInfoListener.onPlaybackPrepared();
            }
            if (quickPlayerListener != null) {
                quickPlayerListener.onPlaybackPrepared();
            }
        }
    };
    private static MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            reportState(COMPLETED);
            if (mainPlayerListener != null) {
                mainPlayerListener.onPlaybackCompleted();
            }
            if (playbackInfoListener != null) {
                playbackInfoListener.onPlaybackCompleted();
            }
        }
    };
    private static MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int whatError, int extraInfo) {
            reportState(ERROR);
            mediaPlayer.stop();
            reportState(STOPPED);
            if (whatError == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                if (playbackInfoListener != null) {
                    playbackInfoListener.onErrorHappened(context.getString(R.string.mediaplayer_unknown_error));
                }
            } else if (whatError == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                if (playbackInfoListener != null) {
                    playbackInfoListener.onErrorHappened(context.getString(R.string.mediaplayer_server_died_error));
                }
            }
            return true;
        }
    };

    private static Context context;

    //  This listener will be the quick player, so it will know when the playback is complete or prepared.
    private static MainPlayerListener mainPlayerListener;

    //  These listeners will be the media browser service.
    private static PlaybackInfoListener playbackInfoListener;

    private static QuickPlayerListener quickPlayerListener;

    public static PlayerHolder getInstance() {
        if (instance == null) {
            instance = new MediaPlayerHolder();
            mediaPlayer = new MediaPlayer();
            reportState(IDLE);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setOnPreparedListener(onPreparedListener);
            mediaPlayer.setOnErrorListener(onErrorListener);
        }
        return instance;
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            reportState(END);
            instance = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            reportState(STARTED);
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            reportState(PAUSED);
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            reportState(STOPPED);
        }
    }

    @Override
    public void loadUrl(String url) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setDataSource(url);
                reportState(INITIALIZED);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                //  TODO: Instead of a toast you could pop up a snackbar
                //  Exception with the URL
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    @Override
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            int position = mediaPlayer.getCurrentPosition();
            return position;
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (mediaPlayer != null) {
            int duration = mediaPlayer.getDuration();
            return duration;
        }
        return 0;
    }

    @Override
    public void setMainPlayerListener(MainPlayerListener mainPlayerListener) {
        MediaPlayerHolder.mainPlayerListener = mainPlayerListener;
    }

    @Override
    public void setPlaybackInfoListener(PlaybackInfoListener playbackInfoListener) {
        MediaPlayerHolder.playbackInfoListener = playbackInfoListener;
    }

    @Override
    public void setQuickPlayerListener(QuickPlayerListener listener) {
        MediaPlayerHolder.quickPlayerListener = listener;
    }

    private static void reportState(int state) {
        if (playbackInfoListener != null) {
            playbackInfoListener.onStateChanged(state);
        }
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void reset() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            reportState(IDLE);
        }
    }
}
