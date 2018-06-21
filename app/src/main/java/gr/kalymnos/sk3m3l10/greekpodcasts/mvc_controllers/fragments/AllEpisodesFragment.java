package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.content.ComponentName;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_episodes.AllEpisodesViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_episodes.AllEpisodesViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackService;

public class AllEpisodesFragment extends Fragment implements AllEpisodesViewMvc.OnEpisodeClickListener,
        AllEpisodesViewMvc.OnPopUpMenuClickListener {

    private static final String TAG = AllEpisodesFragment.class.getSimpleName();

    public interface AllEpisodesFragmentActivityCommunicator{

    }

    private AllEpisodesViewMvc viewMvc;

    private MediaBrowserCompat mediaBrowser;
    private ConnectionCallback connectionCallback;
    private MediaControllerCompat.Callback mediaControllerCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMvc = new AllEpisodesViewMvcImpl(inflater, container);
        viewMvc.setOnEpisodeClickListener(this);
        viewMvc.setOnPopUpMenuClickListener(this);
        //  Display loading bar until the fragment get connected to PlaybackService and fetched mediaItems
        viewMvc.displayLoadingIndicator(true);
        return viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mediaBrowser = new MediaBrowserCompat(getContext(),
                new ComponentName(getActivity().getApplicationContext(), PlaybackService.class),
                connectionCallback = new ConnectionCallback(),
                getArguments());
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
        this.mediaBrowser.disconnect();
    }

    @Override
    public void onEpisodeClick(int position) {
        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPopUpMenuClick(int position) {
        Toast.makeText(getContext(), "Menu Clicked", Toast.LENGTH_SHORT).show();
    }

    private class ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

        @Override
        public void onConnected() {
            MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
            try {
                //  Create controller
                MediaControllerCompat mediaController = new MediaControllerCompat(getContext(), token);
                //  Save controller
                MediaControllerCompat.setMediaController(getActivity(), mediaController);

                //  Subscribe to get the children of the PlaybackService
                //  "It is ok to subscribe while not connected but the results will not be returned
                //  until the connection completes"
                mediaBrowser.subscribe(mediaBrowser.getRoot(), new MediaBrowserCompat.SubscriptionCallback() {

                    //  Important:  In order to receive the callbacks the Service must call notifyChildrenchanged()

                    @Override
                    public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                        viewMvc.displayLoadingIndicator(false);
                        viewMvc.bindEpisodes(children);
                    }

                    @Override
                    public void onError(@NonNull String parentId) {
                        viewMvc.displayLoadingIndicator(false);
                        Toast.makeText(getContext(), "Error fetching episodes!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (RemoteException e) {
                Log.e(TAG, ": Unable to access this media session");
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionSuspended() {
            //  Service has crashed. Disable transport controls until it automatically reconnects
        }

        @Override
        public void onConnectionFailed() {
            //  Service has refused our connection
        }
    }
}
