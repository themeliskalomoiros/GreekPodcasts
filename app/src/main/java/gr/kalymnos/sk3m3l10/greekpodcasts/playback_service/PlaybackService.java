package gr.kalymnos.sk3m3l10.greekpodcasts.playback_service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.PlaybackUtils;

public class PlaybackService extends MediaBrowserServiceCompat implements PlaybackInfoListener {

    /*  Clients should call onPrepareFromMediaId() if the episode is not saved localy.
     *   //  TODO:   Must set a flag to a media item to check if it's downloaded so it can be played directly without preparation.
     *   They can call onPlayFromMediaId() only if the episode is saved in the device.*/

    private static final String TAG = PlaybackService.class.getSimpleName();
    private static final String SESSION_TAG = "MyMediaSession";
    private static final float DEFAULT_PLAYBACK_SPEED = 1f;


    private MediaSessionCompat session;
    private MediaSessionCallback sessionCallback;

    private PlaybackStateCompat.Builder stateBuilder;
    private MediaMetadataCompat.Builder metadataBuilder;
    private Bitmap cachedAlbumArt = null;

    private PlayerHolder player;
    private int reportedPlayerState;
    private String cachedMediaId = null;

    private AsyncTask<String, Void, List<Episode>> fetchEpisodesTask;
    private AsyncTask<String, Void, String> fetchPodcasterNameTask;
    private AsyncTask<String, Void, Bitmap> fetchPosterBitmapTask;    /*Podcast's poster*/
    private List<MediaBrowserCompat.MediaItem> cachedMediaItems;
    private String cachedPodcastersName;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeMediaSession();
        initializePlayer();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        //  Allow anyone to browse this service, by returning something.
        return new BrowserRoot(getString(R.string.app_name), rootHints);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Bundle rootHints = getBrowserRootHints();
        if (areRootHintsValid(rootHints)) {
            //  Detatch to fetch the data in another thread
            result.detach();

            if (fetchEpisodesTask == null) {
                initializeFetchEpisodesTask(result);
            } else {
                //  If an older instance of this thread is running, cancel it
                fetchEpisodesTask.cancel(true);
                fetchEpisodesTask = null;
                initializeFetchEpisodesTask(result);
            }

            if (fetchPodcasterNameTask == null) {
                initializeFetchPodcasterNameTask();
            } else {
                fetchPodcasterNameTask.cancel(true);
                fetchPodcasterNameTask = null;
                initializeFetchPodcasterNameTask();
            }

            if (fetchPosterBitmapTask == null) {
                initializeFetchPosterBitmapTask();
            } else {
                fetchPosterBitmapTask.cancel(true);
                fetchPosterBitmapTask = null;
                initializeFetchPosterBitmapTask();
            }

            /*  Tasks Execution Order:
             *   1 ->    fetchPodcasterNameTask. When has fetched data it executes fetchPosterBitmapTask.
             *   2 ->    fetchPosterBitmapTask.  When has fetched data it executes fetchEpisodesTask
             *   3 ->    fetchEpisodesTask.      When has fetched data it creates mediaItems and sends the result to clients
             *   All the parameters for the tasks are given to the first one.*/
            fetchPodcasterNameTask.execute(new String[]{
                    rootHints.getString(Podcaster.PUSH_ID_KEY)  /*  Pass podcaster push id*/,
                    rootHints.getString(Podcast.POSTER_KEY),    /*  Pass podcast poster url*/
                    rootHints.getString(Episode.EPISODES_KEY)   /*  Pass podcast's episodes push id*/});

            return;
        }
        result.sendResult(null);
    }


    private void initializeFetchPodcasterNameTask() {

        final String[] localTaskParam = new String[1];
        final String[] bitmapTaskParam = new String[1];
        final String[] episodeTaskParam = new String[1];

        fetchPodcasterNameTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {

                localTaskParam[0] = strings[0];
                bitmapTaskParam[0] = strings[1];
                episodeTaskParam[0] = strings[2];

                //  TODO:   Swap with a real service.
                DataRepository repo = new StaticFakeDataRepo();
                return repo.fetchPodcasterName(strings[0]);
            }

            @Override
            protected void onPostExecute(String podcasterName) {
                if (!TextUtils.isEmpty(podcasterName)) {
                    //  An artist name was fetched, cache it to set it to metadata later
                    cachedPodcastersName = podcasterName;
                } else {
                    throw new IllegalArgumentException(TAG + ": Cannot fetch podcaster (artist) name with null parameter");
                }

                if (!TextUtils.isEmpty(bitmapTaskParam[0]) && !TextUtils.isEmpty(episodeTaskParam[0])) {
                    //  Start fetching the bitmap.
                    // Pass also episode task parameter to execute the latter.
                    fetchPosterBitmapTask.execute(bitmapTaskParam[0], episodeTaskParam[0]);
                } else {
                    throw new IllegalArgumentException(TAG + ": Cannot fetch bitmap/episodes with null parameter");
                }

            }
        };
    }

    private void initializeFetchPosterBitmapTask() {
        fetchPosterBitmapTask = new AsyncTask<String, Void, Bitmap>() {

            String episodeTaskParam;

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageUrl = strings[0];
                episodeTaskParam = strings[1];

                Bitmap bitmap = null;

                try {
                    InputStream in = new URL(imageUrl).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    Log.d(TAG, "Error when fetching the stream for " + imageUrl);
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    cachedAlbumArt = bitmap;
                }

                //  Execute the final task which will also send the result
                fetchEpisodesTask.execute(episodeTaskParam);
            }
        };
    }

    private void initializeFetchEpisodesTask(@NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        fetchEpisodesTask = new AsyncTask<String, Void, List<Episode>>() {


            @Override
            protected List<Episode> doInBackground(String... strings) {
                //  TODO:   Replace with a real service.
                DataRepository repo = new StaticFakeDataRepo();
                return repo.fetchPodcastEpisodes(strings[0]);
            }

            @Override
            protected void onPostExecute(List<Episode> episodes) {
                if (episodes != null && episodes.size() > 0) {

                    //  Data is fetched. Create mediaItems for clients
                    List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

                    for (Episode episode : episodes) {

                        Bundle extras = new Bundle();
                        extras.putLong(Episode.DATE_KEY, episode.getDateMilli());
                        extras.putInt(Episode.MINUTES_KEY, episode.getMinutes());
                        extras.putInt(Episode.SECONDS_KEY, episode.getSeconds());

                        MediaDescriptionCompat mediaDescription = new MediaDescriptionCompat
                                .Builder()
                                .setTitle(episode.getTitle())
                                .setMediaId(episode.getFirebasePushId())
                                .setMediaUri(Uri.parse(episode.getUrl()))
                                .setIconBitmap(cachedAlbumArt)
                                .setExtras(extras)
                                .build();
                        mediaItems.add(new MediaBrowserCompat.MediaItem(mediaDescription, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                    }
                    //  MediaItems are ready, cache them for use in onPlayFromMediaId and send the result
                    cachedMediaItems = mediaItems;
                    result.sendResult(mediaItems);
                } else {
                    //  No dataFetched send nothing
                    result.sendResult(null);
                }
            }
        };
    }

    private void initializeMediaSession() {
        session = new MediaSessionCompat(this, SESSION_TAG);

        //  Enable callbacks from MediaButtons and TransportControls
        session.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //  Set an initial PlaybackState with ACTION_PLAY so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        session.setPlaybackState(stateBuilder.build());

        //  Handle callbacks from a MediaController
        session.setCallback(sessionCallback = new MediaSessionCallback());

        setSessionToken(session.getSessionToken());
    }

    @Override
    public void onDurationChanged(int duration) {

    }

    @Override
    public void onPositionChanged(int position) {

    }

    @Override
    public void onStateChanged(int state) {
        //  First cache the state to check whether is valid to take actions
        // on the actual player (MediaPlayer in my case)
        reportedPlayerState = state;

        //  Set the playback state for the session, the metadata will be set inside the sessionCallback methods.
        if (reportedPlayerState == State.STARTED || reportedPlayerState == State.PAUSED || reportedPlayerState == State.STOPPED) {
            int currentPosition = PlaybackUtils.validStateToGetPosition(state) ? player.getCurrentPosition() : 0;
            session.setPlaybackState(PlaybackUtils.getPlaybackStateFromPlayerState(state, currentPosition, DEFAULT_PLAYBACK_SPEED, stateBuilder));
        }
    }

    @Override
    public void onPlaybackPrepared() {
        sessionCallback.onPlayFromMediaId(cachedMediaId, null);
    }

    @Override
    public void onPlaybackCompleted() {

    }

    @Override
    public void onErrorHappened(String errorMessage) {

    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        //  This audioFocusChangeListener pauses playback when audio focus is lost

        private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
                audioFocus -> this.onPause();

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            if (PlaybackUtils.gainedAudioFocus(PlaybackService.this, audioFocusChangeListener)) {
                if (cachedMediaItems != null && cachedMediaItems.size() > 0) {
                    if (!TextUtils.isEmpty(mediaId)) {
                        if (PlaybackUtils.validStateToPlay(reportedPlayerState)) {
                            //  Start this service to be alive. For now it was bound and we don't
                            //  want the playback to stop when all clients unbind
                            startService(new Intent(PlaybackService.this, PlaybackService.class));
                            session.setActive(true);


                            //  Only onPlaybackPrepared() calls this method, so the media is already prepared to be played
                            player.play();


                            //  Set metadata. Playback state is set in onStateChanged(), don't need to do it here
                            updateMetadata(mediaId);
                        }
                    } else {
                        throw new UnsupportedOperationException(TAG + ": Cannot play an item with null or empty mediaId");
                    }
                }
            }
        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            if (cachedMediaItems != null && cachedMediaItems.size() > 0) {
                MediaBrowserCompat.MediaItem item = getMediaItemByMediaId(cachedMediaItems, mediaId);
                if (item != null) {
                    cachedMediaId = mediaId;
                    //  Reseting the player will set its state to State.IDLE, thus validating setDataSource()
                    player.reset();
                    if (PlaybackUtils.validStateToSetDataSource(reportedPlayerState)) {
                        //  When the item is loaded onPlaybackPrepared() will be called
                        player.loadUrl(item.getDescription().getMediaUri().toString());
                    } else {
                        throw new UnsupportedOperationException(TAG + ": Invalid state to prepare the MediaPlayer.");
                    }
                }
            }
        }

        @Override
        public void onPrepare() {
            if (cachedMediaItems != null && cachedMediaItems.size() > 0) {
                //  Just play the first item
                MediaBrowserCompat.MediaItem item = cachedMediaItems.get(0);
                cachedMediaId = item.getMediaId();
                //  Reseting the player will set its state to State.IDLE, thus validating setDataSource()
                player.reset();
                if (PlaybackUtils.validStateToSetDataSource(reportedPlayerState)) {
                    //  When the item is loaded onPlaybackPrepared() will be called
                    player.loadUrl(item.getDescription().getMediaUri().toString());
                } else {
                    throw new UnsupportedOperationException(TAG + ": Invalid state to prepare the MediaPlayer.");
                }
            }
        }

        @Override
        public void onPause() {
            if (PlaybackUtils.validStateToPause(reportedPlayerState)) {
                player.pause();
                updateMetadata(cachedMediaId);
            }
        }

        @Override
        public void onStop() {
            if (PlaybackUtils.validStateToStop(reportedPlayerState)) {
                player.stop();
                updateMetadata(cachedMediaId);

                PlaybackUtils.abandonAudioFocus(PlaybackService.this, audioFocusChangeListener);
                session.setActive(false);
                PlaybackService.this.stopSelf();
            } else {
                throw new UnsupportedOperationException(TAG + ": Invalid state to pause the MediaPlayer.");
            }
        }

        @Override
        public void onSkipToNext() {
            int currentMediaItemIndex = getCurrentMediaItemIndex(cachedMediaItems, cachedMediaId);
            int nextMediaIndex = currentMediaItemIndex - 1;
            if (nextMediaIndex < cachedMediaItems.size()) {
                //  There is a next media item, cached its mediaId and prepare it to play
                cachedMediaId = cachedMediaItems.get(nextMediaIndex).getMediaId();
                onPrepareFromMediaId(cachedMediaId, null);
            } else {
                //  There is no previous media item
                Toast.makeText(PlaybackService.this, R.string.no_next_episode_msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onSkipToPrevious() {
            int currentMediaItemIndex = getCurrentMediaItemIndex(cachedMediaItems, cachedMediaId);
            int previousMediaIndex = currentMediaItemIndex - 1;
            if (previousMediaIndex >= 0) {
                //  There is a previous media item, cached its mediaId and prepare it to play
                cachedMediaId = cachedMediaItems.get(previousMediaIndex).getMediaId();
                onPrepareFromMediaId(cachedMediaId, null);
            } else {
                //  There is no previous media item
                Toast.makeText(PlaybackService.this, R.string.no_previous_episode_msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }

        private void updateMetadata(String mediaId) {
            MediaBrowserCompat.MediaItem item = getMediaItemByMediaId(cachedMediaItems, mediaId);
            long duration = PlaybackUtils.validStateToGetDuration(reportedPlayerState) ? player.getDuration() : 0;

            session.setMetadata(metadataBuilder
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cachedPodcastersName)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, cachedAlbumArt)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, item.getDescription().getTitle().toString())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DATE, item.getDescription().getExtras().getLong(Episode.DATE_KEY))
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                    .build());
        }

        private int getCurrentMediaItemIndex(List<MediaBrowserCompat.MediaItem> items, String mediaId) {
            if (items != null && items.size() > 0) {

                //  Start with an invalid index in case the list does not contain a mediaItem
                //  that matches the mediaId (if we returned 0 it would be a lie because that's
                //  the first item in the list).
                int index = -1;

                for (int i = 0; i < items.size(); i++) {
                    MediaBrowserCompat.MediaItem temp = items.get(i);
                    if (temp.getMediaId().equals(cachedMediaId)) {
                        //  Corect index found, leave loop
                        index = i;
                        break;
                    }
                }

                if (index >= 0) {
                    return index;
                } else {
                    throw new UnsupportedOperationException(TAG + ": getCurrentMediaItemIndex() returned illegal index.");
                }
            } else {
                throw new UnsupportedOperationException(TAG + ": Can't get Current MediaItem Index if the items list is null.");
            }
        }

    }

    private MediaBrowserCompat.MediaItem getMediaItemByMediaId(List<MediaBrowserCompat.MediaItem> list, String id) {
        for (MediaBrowserCompat.MediaItem item : list) {
            if (item.getMediaId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    private void initializePlayer() {
        player = new MediaPlayerHolder(this);
        player.setPlaybackInfoListener(this);
    }

    private boolean areRootHintsValid(Bundle rootHints) {
        //  One key to fetch all the episodes of the podcast, one key for the podcast poster
        //  and one key to fetch the podcaster (artist) name.
        if (rootHints != null && rootHints.containsKey(Episode.EPISODES_KEY)
                && rootHints.containsKey(Podcast.POSTER_KEY)
                && rootHints.containsKey(Podcaster.PUSH_ID_KEY)) {
            return true;
        }
        return false;
    }
}