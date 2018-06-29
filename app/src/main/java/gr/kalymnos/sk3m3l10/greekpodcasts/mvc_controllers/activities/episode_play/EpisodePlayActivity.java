package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.episode_play;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.SeekBar;
import android.widget.Toast;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.PodcasterActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.episode_play_screen.EpisodePlayViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.episode_play_screen.EpisodePlayViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackService;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.LocalDatabaseUtils;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.PlaybackUtils;

public class EpisodePlayActivity extends AppCompatActivity implements EpisodePlayViewMvc.OnActionButtonsClickListener,
        EpisodePlayViewMvc.OnTransportControlsClickListener, EpisodePlayViewMvc.OnPodcasterClickListener, SeekBar.OnSeekBarChangeListener,
        DownloadAudioService.OnDownloadAudioFileListener {

    private static final long SEEKBAR_UPDATE_INTERVAL = 500;
    private EpisodePlayViewMvc viewMvc;

    private MediaBrowserCompat mediaBrowser;
    private ConnectionCallback connectionCallback;
    private MediaControllerCompat.Callback mediaControllerCallback;

    private Thread updateMediaBarTask;

    private int cachedCurrentEpisodeLocalDbId;

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
        //  Make sure the thread is stoped
        stopUpdatingMediabar();
        if (MediaControllerCompat.getMediaController(this) != null) {
            MediaControllerCompat.getMediaController(this).unregisterCallback(mediaControllerCallback);
        }
        this.mediaBrowser.disconnect();
    }

    @Override
    public void onStarClick() {
        int podcastLocalDbId = getIntent().getExtras().getInt(Podcast.LOCAL_DB_ID_KEY);

        Runnable starPodcastAction = () -> DatabaseOperations.starPodcastTask(this, podcastLocalDbId, true,
                () -> viewMvc.drawStarButton(), () -> viewMvc.unDrawStarButton()).execute();

        Runnable unStarPodcastAction = () -> DatabaseOperations.starPodcastTask(this, podcastLocalDbId, false,
                () -> viewMvc.drawStarButton(), () -> viewMvc.unDrawStarButton()).execute();

        DatabaseOperations.clickPodcastTask(this, podcastLocalDbId, starPodcastAction, unStarPodcastAction)
                .execute();
    }

    @Override
    public void onDownloadClick() {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        DatabaseOperations.cacheCurrentEpisodeLocalDbIdTask(this, getCurrentEpisodePushId(),
                getIntent().getExtras().getInt(Podcast.LOCAL_DB_ID_KEY),
                () -> DownloadAudioService.startActionDownloadAudio(this, getCurrentEpisodeUrl(), cachedCurrentEpisodeLocalDbId, getCurrentEpisodeName(), this))
                .execute();
    }

    @Override
    public void onInfoClick() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayButtonClick() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController != null) {
            PlaybackStateCompat state = mediaController.getPlaybackState();
            boolean statePausedOrStoppedOrNone = state.getState() == PlaybackStateCompat.STATE_PAUSED
                    || state.getState() == PlaybackStateCompat.STATE_STOPPED
                    || state.getState() == PlaybackStateCompat.STATE_NONE;  /*  Why state none? Because if the playback is stoped the service is calling stopSelf() and that results to state none!*/

            MediaMetadataCompat metadata = mediaController.getMetadata();
            long duration = 0;
            if (metadata != null) {
                duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            }

            if (state != null && statePausedOrStoppedOrNone) {

                //  Typically because of the time interval where the thread updates the seekbar
                //  the progress of the seekbar may not be exactly the same as the duration,
                //  but if it's less than a second its almost the same
                boolean positionEqualsDuration = Math.abs(viewMvc.getSeekBarProgress() - duration) < 800;
                if (positionEqualsDuration) {
                    //  media bar reached the end, reset it to seekTo from the begining
                    viewMvc.resetSeekBarProgress();
                }

                //  First seek to the position of the media bar and then play the track from there
                mediaController.getTransportControls().seekTo(viewMvc.getSeekBarProgress());
                mediaController.getTransportControls().play();
            }
        }
    }

    @Override
    public void onPauseButtonClick() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController != null) {
            PlaybackStateCompat state = mediaController.getPlaybackState();
            if (state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mediaController.getTransportControls().pause();
            }
        }
    }

    @Override
    public void onSkipToNextButtonClick() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController != null) {
            mediaController.getTransportControls().skipToNext();
        }
    }

    @Override
    public void onSkipToPreviousButtonClick() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController != null) {
            mediaController.getTransportControls().skipToPrevious();
        }
    }

    @Override
    public void onPodcasterClick() {
        Intent intent = new Intent(this, PodcasterActivity.class);
        intent.putExtra(Podcaster.PUSH_ID_KEY, getIntent().getExtras().getString(Podcaster.PUSH_ID_KEY));
        startActivity(intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
            PlaybackStateCompat state = mediaController.getPlaybackState();

            if (state != null) {
                if (state.getState() == PlaybackStateCompat.STATE_NONE) {
                    //  This state was set after onStop() because service called stopSelf()
                    //  There is no playback, though the track is allready prepared so
                    //  just bind the position
                    seekBar.setProgress(progress);
                    viewMvc.bindPlaybackPosition(PlaybackUtils.playbackPositionString(progress));
                } else {
                    mediaController.getTransportControls().seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onDownloadCompleted(String episodeName) {
        this.runOnUiThread(() -> viewMvc.drawDownloadButton());
    }

    @Override
    public void onDownloadError(String errorMessage) {
        this.runOnUiThread(() -> viewMvc.unDrawDownloadButton());
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
                            startUpdatingMediabar();
                        } else {
                            viewMvc.displayPlayButton(true);
                            stopUpdatingMediabar();
                        }
                    }

                    @Override
                    public void onMetadataChanged(MediaMetadataCompat metadata) {
                        updateUiFromMetadata(metadata);
                    }
                });

                //  Initial binding of UI
                PlaybackStateCompat state = mediaController.getPlaybackState();
                if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    viewMvc.displayPlayButton(false);
                    startUpdatingMediabar();
                } else {
                    viewMvc.displayPlayButton(true);
                    stopUpdatingMediabar();
                }

                MediaMetadataCompat metadata = mediaController.getMetadata();
                if (metadata != null) {
                    updateUiFromMetadata(metadata);
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

        private void updateUiFromMetadata(MediaMetadataCompat metadata) {
            viewMvc.bindEpisodeTitle(metadata.getDescription().getTitle().toString());
            viewMvc.bindPodcaster(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
            viewMvc.bindPoster(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
            long durationMilli = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            viewMvc.bindSeekBarMax((int) durationMilli);
            viewMvc.bindPlaybackDuration(PlaybackUtils.playbackPositionString(durationMilli));

            DatabaseOperations.isDownloadedTask(EpisodePlayActivity.this,
                    getCurrentEpisodePushId(), getIntent().getExtras().getInt(Podcast.LOCAL_DB_ID_KEY),
                    () -> viewMvc.drawDownloadButton(),
                    () -> viewMvc.unDrawDownloadButton())
                    .execute();
        }
    }

    private void initializeUpdateMediaBarTask() {
        updateMediaBarTask = new Thread(() -> {
            MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(EpisodePlayActivity.this);

            while (true) {
                try {
                    Thread.sleep(SEEKBAR_UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    //  This thread has being interrupted, terminate
                    return;
                }

                PlaybackStateCompat state = mediaController.getPlaybackState();
                long position = state.getPosition();

                runOnUiThread(() -> {
                    viewMvc.bindSeekBarProgress((int) position);
                    viewMvc.bindPlaybackPosition(PlaybackUtils.playbackPositionString(position));
                });
            }
        });
    }

    private void startUpdatingMediabar() {
        if (updateMediaBarTask == null) {
            initializeUpdateMediaBarTask();
        }

        if (!updateMediaBarTask.isAlive()) {
            updateMediaBarTask.start();
        }
    }

    private String getCurrentEpisodePushId() {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            MediaMetadataCompat metadata = controller.getMetadata();
            if (metadata != null) {
                return metadata.getDescription().getMediaId();
            }
        }
        return null;
    }

    private String getCurrentEpisodeUrl() {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            MediaMetadataCompat metadata = controller.getMetadata();
            if (metadata != null) {
                return metadata.getDescription().getMediaUri().toString();
            }
        }
        return null;
    }

    private String getCurrentEpisodeName() {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            MediaMetadataCompat metadata = controller.getMetadata();
            if (metadata != null) {
                return metadata.getDescription().getTitle().toString();
            }
        }
        return null;
    }

    private void stopUpdatingMediabar() {
        if (updateMediaBarTask != null) {
            updateMediaBarTask.interrupt();
        }
        updateMediaBarTask = null;
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
        DatabaseOperations.isPodcastStarredTask(this,
                getIntent().getExtras().getInt(Podcast.LOCAL_DB_ID_KEY),
                () -> viewMvc.drawStarButton(),
                () -> viewMvc.unDrawStarButton()).execute();
    }

    private static class DatabaseOperations {

        private static final int ONE_EPISODE = 1;
        private static final int INVALID_ID = -1;
        private static final String TAG = DatabaseOperations.class.getSimpleName();

        static AsyncTask<Void, Void, Cursor> isPodcastStarredTask(@NonNull Activity activity, int podcastLocalDatabaseId, Runnable drawStarAction, Runnable undrawStarAction) {
            return new AsyncTask<Void, Void, Cursor>() {

                @Override
                protected Cursor doInBackground(Void... voids) {
                    return LocalDatabaseUtils.queryPodcast(activity, podcastLocalDatabaseId);
                }

                @Override
                protected void onPostExecute(Cursor cursor) {
                    if (cursor != null && cursor.getCount() == ONE_EPISODE) {

                        cursor.moveToFirst();

                        int starredIndex = cursor.getColumnIndex(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_STARRED);
                        boolean isPodcastStarred = cursor.getInt(starredIndex) == 0 ? false : true;

                        if (isPodcastStarred) {
                            activity.runOnUiThread(drawStarAction);
                        } else {
                            activity.runOnUiThread(undrawStarAction);
                        }

                    } else {
                        throw new UnsupportedOperationException(TAG + ": Null cursor or its size is 0.");
                    }
                }
            };
        }

        static AsyncTask<Void, Void, Cursor> clickPodcastTask(@NonNull Activity activity, int podcastLocalDatabaseId, Runnable starPodcastAction, Runnable unStarPodcastAction) {
            return new AsyncTask<Void, Void, Cursor>() {

                @Override
                protected Cursor doInBackground(Void... voids) {
                    return LocalDatabaseUtils.queryPodcast(activity, podcastLocalDatabaseId);
                }

                @Override
                protected void onPostExecute(Cursor cursor) {
                    if (cursor != null && cursor.getCount() == ONE_EPISODE) {
                        cursor.moveToFirst();
                        boolean isPodcastStarred = cursor.getInt(cursor.getColumnIndex(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_STARRED)) != 0;
                        if (isPodcastStarred) {
                            activity.runOnUiThread(unStarPodcastAction);
                        } else {
                            activity.runOnUiThread(starPodcastAction);
                        }
                    } else {
                        throw new UnsupportedOperationException(TAG + ": Cursor is null or 0 size.");
                    }
                }
            };
        }

        static AsyncTask<Void, Void, Integer> starPodcastTask(@NonNull Activity activity, int podcastLocalDatabaseId,
                                                              boolean setStarred, Runnable drawStarAction, Runnable undrawStarAction) {
            return new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... voids) {
                    ContentValues values = new ContentValues();
                    values.put(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_STARRED, setStarred);
                    return LocalDatabaseUtils.updatePodcastTask(activity, podcastLocalDatabaseId, values);
                }

                @Override
                protected void onPostExecute(Integer integer) {
                    if (integer.intValue() == ONE_EPISODE) {
                        //  Podcast updated successfuly
                        if (setStarred) {
                            activity.runOnUiThread(drawStarAction);
                        } else {
                            activity.runOnUiThread(undrawStarAction);
                        }
                    }
                }
            };
        }

        static AsyncTask<Void, Void, Boolean> isDownloadedTask(@NonNull Activity activity, @NonNull String episodePushId, int podcastLocalDbId,
                                                               Runnable actionIfDownloaded, Runnable actionIfNotDownloaded) {
            return new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    Cursor cursor = LocalDatabaseUtils.queryEpisode(activity, episodePushId, podcastLocalDbId);
                    if (cursor != null && cursor.getCount() == ONE_EPISODE) {
                        cursor.moveToFirst();
                        int uriColumnIndex = cursor.getColumnIndex(UserMetadataContract.EpisodeEntry.COLUMN_NAME_DOWNLOADED_URI);
                        String episodeUri = cursor.getString(uriColumnIndex);
                        if (!TextUtils.isEmpty(episodeUri)) {
                            return true;
                        }
                        return false;
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean uriExists) {
                    if (uriExists) {
                        activity.runOnUiThread(actionIfDownloaded);
                    } else {
                        activity.runOnUiThread(actionIfNotDownloaded);
                    }
                }
            };
        }

        static AsyncTask<Void, Void, Integer> cacheCurrentEpisodeLocalDbIdTask(@NonNull Activity activity,
                                                                               @NonNull String episodePushId, int podcastLocalDbId, Runnable action) {
            return new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    Cursor cursor = LocalDatabaseUtils.queryEpisode(activity, episodePushId, podcastLocalDbId);
                    if (cursor != null && cursor.getCount() == ONE_EPISODE) {
                        cursor.moveToFirst();
                        int idIndex = cursor.getColumnIndex(UserMetadataContract.EpisodeEntry._ID);
                        return cursor.getInt(idIndex);
                    }
                    return INVALID_ID;
                }

                @Override
                protected void onPostExecute(Integer id) {
                    if (id != INVALID_ID && activity instanceof EpisodePlayActivity) {
                        //  First cache the id because action is probably going to use it
                        EpisodePlayActivity episodePlayActivity = (EpisodePlayActivity) activity;
                        episodePlayActivity.cachedCurrentEpisodeLocalDbId = id;

                        activity.runOnUiThread(action);
                    } else {
                        throw new UnsupportedOperationException(TAG + ": INVALID_ID or activity not an EpisodePlayActivity isntance.");
                    }
                }
            };
        }
    }
}
