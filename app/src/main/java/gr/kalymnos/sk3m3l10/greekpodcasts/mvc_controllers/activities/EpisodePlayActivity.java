package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.content.ComponentName;
import android.media.AudioManager;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.SeekBar;
import android.widget.Toast;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.episode_play_screen.EpisodePlayViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.episode_play_screen.EpisodePlayViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackService;

public class EpisodePlayActivity extends AppCompatActivity implements EpisodePlayViewMvc.OnActionButtonsClickListener,
        EpisodePlayViewMvc.OnTransportControlsClickListener, EpisodePlayViewMvc.OnPodcasterClickListener, SeekBar.OnSeekBarChangeListener {

    private EpisodePlayViewMvc viewMvc;

    private MediaBrowserCompat mediaBrowser;
    private ConnectionCallback connectionCallback;
    private MediaControllerCompat.Callback mediaControllerCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewMvc();
        setContentView(viewMvc.getRootView());
        setUpMediaBrowser();
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (MediaControllerCompat.getMediaController(this) != null) {
            MediaControllerCompat.getMediaController(this).unregisterCallback(mediaControllerCallback);
        }
        this.mediaBrowser.disconnect();
    }

    @Override
    public void onStarClick() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadClick() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoClick() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayButtonClick() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController!=null){
            PlaybackStateCompat state = mediaController.getPlaybackState();
            if (state!=null && state.getState()==PlaybackStateCompat.STATE_PAUSED){
                mediaController.getTransportControls().play();
            }
        }
    }

    @Override
    public void onPauseButtonClick() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController!=null){
            PlaybackStateCompat state = mediaController.getPlaybackState();
            if (state!=null && state.getState()==PlaybackStateCompat.STATE_PLAYING){
                mediaController.getTransportControls().pause();
            }
        }
    }

    @Override
    public void onSkipToNextButtonClick() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSkipToPreviousButtonClick() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPodcasterClick() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

        @Override
        public void onConnected() {

            try {
                MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
                //  Create controller
                MediaControllerCompat mediaController = new MediaControllerCompat(EpisodePlayActivity.this, token);
                //  Save controller
                MediaControllerCompat.setMediaController(EpisodePlayActivity.this, mediaController);

                mediaController.registerCallback(mediaControllerCallback = new MediaControllerCompat.Callback() {
                    @Override
                    public void onSessionReady() {
                        viewMvc.disableTransportControls(false);
                    }

                    @Override
                    public void onSessionDestroyed() {
                        viewMvc.disableTransportControls(true);
                        //  TODO:   Display a better message
                        Toast.makeText(EpisodePlayActivity.this, "Debug: Session destroyed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPlaybackStateChanged(PlaybackStateCompat state) {
                        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                            viewMvc.displayPlayButton(false);
                        } else {
                            viewMvc.displayPlayButton(true);
                        }
                    }

                    @Override
                    public void onMetadataChanged(MediaMetadataCompat metadata) {
                        viewMvc.bindEpisodeTitle(metadata.getDescription().getTitle().toString());
                        viewMvc.bindPodcaster(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
                        viewMvc.bindPoster(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
                        viewMvc.bindSeekBarMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
                    }
                });

                //  Initial binding of UI
                PlaybackStateCompat state = mediaController.getPlaybackState();
                if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    viewMvc.displayPlayButton(false);
                } else {
                    viewMvc.displayPlayButton(true);
                }

                MediaMetadataCompat metadata = mediaController.getMetadata();
                if (metadata != null) {
                    viewMvc.bindEpisodeTitle(metadata.getDescription().getTitle().toString());
                    viewMvc.bindPodcaster(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
                    viewMvc.bindPoster(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
                    viewMvc.bindSeekBarMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionSuspended() {
            //  Service has crashed. Disable transport controls until it automatically reconnects
            viewMvc.disableTransportControls(false);
            viewMvc.displayPlayButton(true);
        }

        @Override
        public void onConnectionFailed() {
            //  Service has refused our connection
        }


    }

    private void setUpMediaBrowser() {
        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(getApplicationContext(), PlaybackService.class),
                connectionCallback = new ConnectionCallback(), null);
    }

    private void initializeViewMvc() {
        viewMvc = new EpisodePlayViewMvcImpl(LayoutInflater.from(this), null);
        viewMvc.setOnActionButtonsClickListener(this);
        viewMvc.setOnPodcasterClickListener(this);
        viewMvc.setOnTransportControlsClickListener(this);
        viewMvc.setSeekBarChangeListener(this);
        setSupportActionBar(viewMvc.getToolbar());
    }
}
