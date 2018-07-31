package gr.kalymnos.sk3m3l10.greekpodcasts.playback_service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.COMPLETED;
import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.END;
import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.ERROR;
import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.IDLE;
import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.INITIALIZED;
import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.PAUSED;
import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.PREPARED;
import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.STARTED;
import static gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackInfoListener.State.STOPPED;

class MediaPlayerHolder implements PlayerHolder, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private Context context;
    private MediaPlayer mediaPlayer;
    private PlaybackInfoListener playbackInfoListener;

    MediaPlayerHolder(Context context) {
        mediaPlayer = new MediaPlayer();
        reportState(IDLE);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        this.context = context;
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
    public void reset() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            reportState(IDLE);
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            reportState(END);
        }
    }

    @Override
    public void setPlaybackInfoListener(PlaybackInfoListener listener) {
        playbackInfoListener = listener;
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
    public void loadUri(Uri uri) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setDataSource(context,uri,null);
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
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
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

    private void reportState(int state) {
        if (playbackInfoListener != null) {
            playbackInfoListener.onStateChanged(state);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        reportState(COMPLETED);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int whatError, int extraInfo) {
        reportState(ERROR);
        mediaPlayer.reset();
        reportState(IDLE);

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

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        reportState(PREPARED);
        playbackInfoListener.onPlaybackPrepared();
    }
}
