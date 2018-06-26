package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.EpisodePlayActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.quick_player.QuickPlayerViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.quick_player.QuickPlayerViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackService;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;

public class QuickPlayerFragment extends Fragment implements QuickPlayerViewMvc.OnTransportControlsClickListener, QuickPlayerViewMvc.OnQuickPlayerClickListener {
    private static final String TAG = QuickPlayerFragment.class.getSimpleName();

    private QuickPlayerViewMvc viewMvc;

    private MediaBrowserCompat mediaBrowser;
    private ConnectionCallback connectionCallback;
    private MediaControllerCompat.Callback mediaControllerCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeViewMvc(inflater, container);
        return viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mediaBrowser = new MediaBrowserCompat(getContext(),
                new ComponentName(getActivity().getApplicationContext(), PlaybackService.class),
                connectionCallback = new ConnectionCallback(), null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (MediaControllerCompat.getMediaController(getActivity())!=null){
            MediaControllerCompat.getMediaController(getActivity()).unregisterCallback(mediaControllerCallback);
        }
        this.mediaBrowser.disconnect();
    }

    private void initializeViewMvc(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        viewMvc = new QuickPlayerViewMvcImpl(inflater, container);
        viewMvc.setOnTransportControlsClickListener(this);
        viewMvc.setOnRootClickListener(this);
    }

    @Override
    public void onPlayButtonClick() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());
        if (mediaController!=null){
            PlaybackStateCompat state = mediaController.getPlaybackState();
            if (state!=null && state.getState()==PlaybackStateCompat.STATE_PAUSED){
                mediaController.getTransportControls().play();
            }
        }
    }

    @Override
    public void onPauseButtonClick() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());
        if (mediaController!=null){
            PlaybackStateCompat state = mediaController.getPlaybackState();
            if (state!=null && state.getState()==PlaybackStateCompat.STATE_PLAYING){
                mediaController.getTransportControls().pause();
            }
        }
    }

    @Override
    public void onQuickPlayerClick() {
        Intent intent = new Intent(getContext(), EpisodePlayActivity.class);
        intent.putExtra(Podcaster.PUSH_ID_KEY,getArguments().getString(Podcaster.PUSH_ID_KEY));
        intent.putExtra(Podcast.PODCAST_KEY,getArguments().getString(Podcast.PODCAST_KEY));
        intent.putExtra(Podcast.LOCAL_DB_ID_KEY,getArguments().getInt(Podcast.LOCAL_DB_ID_KEY));

        startActivity(intent);
    }

    private class ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        @Override
        public void onConnected() {
            try {
                MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
                //  Create controller
                MediaControllerCompat mediaController = new MediaControllerCompat(getContext(), token);
                //  Save controller
                MediaControllerCompat.setMediaController(getActivity(), mediaController);

                mediaController.registerCallback(mediaControllerCallback = new MediaControllerCompat.Callback() {
                    @Override
                    public void onSessionReady() {
                        viewMvc.disableRoot(false);
                        viewMvc.disableTransportControls(false);
                    }

                    @Override
                    public void onSessionDestroyed() {
                        viewMvc.disableRoot(true);
                        viewMvc.disableTransportControls(true);

                        //  TODO:   Display a better message
                        Toast.makeText(getContext(), "Debug: Session destroyed", Toast.LENGTH_SHORT).show();
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
                        viewMvc.bindPodcastPoster(metadata.getDescription().getIconBitmap());
                        bindPlayerBackgroundColor(viewMvc.getPosterBitmap());
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
                    viewMvc.bindPodcastPoster(metadata.getDescription().getIconBitmap());
                }

            } catch (RemoteException e) {
                Log.e(TAG, ": Unable to access this media session");
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionSuspended() {
            //  Service has crashed. Disable transport controls until it automatically reconnects
            viewMvc.disableRoot(true);
            viewMvc.disableTransportControls(true);
        }

        @Override
        public void onConnectionFailed() {
            //  Service has refused our connection
        }
    }

    private void bindPlayerBackgroundColor(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            //  Palette generated
            int lightVibrantColor = palette.getLightVibrantColor(
                    getActivity().getResources().getColor(QuickPlayerViewMvc.DEFAULT_BACKGROUND_COLOR));
            viewMvc.bindBackgroundColor(lightVibrantColor);
        });
    }
}
