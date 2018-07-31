package gr.kalymnos.sk3m3l10.greekpodcasts.playback_service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.BitmapUtils;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.DateUtils;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.PlaybackUtils;
import gr.kalymnos.sk3m3l10.greekpodcasts.widget.PlaybackWidget;

public class PlaybackService extends MediaBrowserServiceCompat implements PlaybackInfoListener {

    /*  Clients should call onPrepareFromMediaId() if the episode is not saved localy.
     *   //  TODO:   Must set a flag to a media item to check if it's downloaded so it can be played directly without preparation.
     *   They can call onPlayFromMediaId() only if the episode is saved in the device.*/

    private static final String TAG = PlaybackService.class.getSimpleName();
    private static final String SESSION_TAG = "MyMediaSession";
    private static final float DEFAULT_PLAYBACK_SPEED = 1f;
    private static final int FOREGROUND_ID = 112;

    private static final String ACTION_UPDATE_WIDGETS = PlaybackService.class.getCanonicalName() + "action_update_widgets";
    public static final String LOAD_FROM_URI_KEY = "load from uri key";

    private MediaSessionCompat session;
    private MediaSessionCallback sessionCallback;

    private PlaybackStateCompat.Builder stateBuilder;
    private MediaMetadataCompat.Builder metadataBuilder;
    private Bitmap cachedAlbumArt = null;

    private PlayerHolder player;
    private int reportedPlayerState;
    private String cachedMediaId = null;

    private List<MediaBrowserCompat.MediaItem> cachedMediaItems;
    private String cachedPodcastersName;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeMediaSession();
        initializePlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isActionToUpdateWidgets = intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_UPDATE_WIDGETS);

        if (isActionToUpdateWidgets) {
            handleActionUpdateWidgets();
        }

        MediaButtonReceiver.handleIntent(session, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleActionUpdateWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PlaybackWidget.class));
        MediaControllerCompat mediaController = session.getController();
        if (mediaController != null) {
            PlaybackStateCompat playbackState = mediaController.getPlaybackState();
            MediaMetadataCompat metaData = mediaController.getMetadata();

            if (playbackState != null && metaData != null) {
                PlaybackWidget.updateAllWidgets(this, appWidgetManager, appWidgetIds, playbackState.getState(), metaData.getDescription().getTitle().toString(), metaData.getDescription().getIconBitmap());
            }

        }
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

            Podcast podcast = rootHints.getParcelable(Podcast.PODCAST_KEY);

            if (podcast != null) {
                //  Detatch to fetch the data in another thread
                result.detach();

                String podcasterPushId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference()
                        .child(ChildNames.PODCASTERS)
                        .child(podcasterPushId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Podcaster podcaster = dataSnapshot.getValue(Podcaster.class);
                                if (podcaster != null) {
                                    //  An artist name was fetched, cache it to set it to metadata later
                                    cachedPodcastersName = podcaster.getUsername();
                                    fetchPosterBitmapAndAfterEpisodes();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                            private void fetchPosterBitmapAndAfterEpisodes() {
                                AsyncTask<Void, Void, Bitmap> fetchBitmapFromNetworkTask = new AsyncTask<Void, Void, Bitmap>() {
                                    @Override
                                    protected Bitmap doInBackground(Void... voids) {
                                        try {
                                            return BitmapUtils.bitmapFromUrl(new URL(podcast.getPosterUrl()));
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                            return null;
                                        }
                                    }

                                    @Override
                                    protected void onPostExecute(Bitmap bitmap) {
                                        if (bitmap != null) {
                                            cachedAlbumArt = bitmap;
                                            fetchEpisodes();
                                        }
                                    }
                                };

                                fetchBitmapFromNetworkTask.execute();
                            }

                            private void fetchEpisodes() {
                                FirebaseDatabase.getInstance().getReference()
                                        .child(ChildNames.EPISODES)
                                        .child(podcast.getFirebasePushId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        List<Episode> episodeList = new ArrayList<>();

                                        for (DataSnapshot episodeSnapshot : dataSnapshot.getChildren()) {
                                            Episode episode = episodeSnapshot.getValue(Episode.class);
                                            if (episode != null) {
                                                episode.setFirebasePushId(episodeSnapshot.getKey());
                                                episodeList.add(episode);
                                            }
                                        }

                                        if (episodeList.size() > 0) {
                                            result.sendResult(createMediaItems(episodeList));
                                        } else {
                                            //  No dataFetched send nothing
                                            result.sendResult(null);
                                        }
                                    }

                                    private List<MediaBrowserCompat.MediaItem> createMediaItems(List<Episode> episodeList) {
                                        //  Data is fetched. Create mediaItems for clients
                                        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

                                        for (Episode episode : episodeList) {

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
                                        return cachedMediaItems = mediaItems;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

            } else {
                throw new UnsupportedOperationException(TAG + ": Podcast should not be null.");
            }
        }
    }

    private void initializeMediaSession() {
        session = new MediaSessionCompat(this, SESSION_TAG);

        //  Enable callbacks from MediaButtons and TransportControls
        session.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        session.setMediaButtonReceiver(null);

        //  Set an initial PlaybackState with ACTION_PLAY so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        session.setPlaybackState(stateBuilder.build());

        metadataBuilder = new MediaMetadataCompat.Builder();

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
        if (reportedPlayerState == State.STARTED || reportedPlayerState == State.PAUSED || reportedPlayerState == State.STOPPED
                || reportedPlayerState == State.COMPLETED) {
            int currentPosition = PlaybackUtils.validStateToGetPosition(state) ? player.getCurrentPosition() : 0;
            session.setPlaybackState(PlaybackUtils.getPlaybackStateFromPlayerState(state, currentPosition, DEFAULT_PLAYBACK_SPEED, stateBuilder));
            startServiceToUpdateWidgets(this);
        }
    }

    @Override
    public void onPlaybackPrepared() {
        sessionCallback.onPlayFromMediaId(cachedMediaId, null);
    }

    @Override
    public void onPlaybackCompleted() {
        if (PlaybackUtils.validStateToStop(reportedPlayerState)) {
            sessionCallback.onStop();
        }
    }

    @Override
    public void onErrorHappened(String errorMessage) {

    }

    public static void startServiceToUpdateWidgets(Context context) {
        Intent intent = new Intent(context, PlaybackService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        //  This audioFocusChangeListener pauses playback when audio focus is lost

        private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
                audioFocus -> this.onPause();

        @Override
        public void onPlay() {
            if (PlaybackUtils.validStateToPlay(reportedPlayerState)) {
                player.play();
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            if (PlaybackUtils.gainedAudioFocus(PlaybackService.this, audioFocusChangeListener)) {
                if (cachedMediaItems != null && cachedMediaItems.size() > 0) {
                    if (!TextUtils.isEmpty(mediaId)) {
                        if (PlaybackUtils.validStateToPlay(reportedPlayerState)) {
                            //  Start this service to be alive. For now it was bound and we don't
                            //  want the playback to stop when all clients unbind
                            startServiceToUpdateWidgets(PlaybackService.this);
                            session.setActive(true);

                            //  Only onPlaybackPrepared() calls this method, so the media is already prepared to be played
                            player.play();

                            //  Set metadata. Playback state is set in onStateChanged(), don't need to do it here
                            updateMetadata(mediaId);

                            showMediaStyleNotification();
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

                        if (extras!=null && extras.containsKey(LOAD_FROM_URI_KEY)){
                            player.loadUri(Uri.parse(extras.getString(LOAD_FROM_URI_KEY)));
                        }else{
                            player.loadUrl(item.getDescription().getMediaUri().toString());
                        }

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
            int nextMediaIndex = currentMediaItemIndex + 1;
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
            if (PlaybackUtils.validStateToSeek(reportedPlayerState)) {
                player.seekTo((int) pos);
                //  Usually the state is updated in onStateChanged(), though with a seek action the
                //  actual player does not change its state (from STATE_PLAY after a seekTo action
                //  the state is again STATE_PLAY). So this is a state refresher so the clients will
                //  be able to update UI after a seekTo action.
                session.setPlaybackState(PlaybackUtils.getPlaybackStateFromPlayerState(reportedPlayerState, pos, DEFAULT_PLAYBACK_SPEED, stateBuilder));
            }
        }

        private void updateMetadata(String mediaId) {
            MediaBrowserCompat.MediaItem item = getMediaItemByMediaId(cachedMediaItems, mediaId);
            long duration = PlaybackUtils.validStateToGetDuration(reportedPlayerState) ? player.getDuration() : 0;

            session.setMetadata(metadataBuilder
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cachedPodcastersName)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, cachedAlbumArt)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, item.getDescription().getTitle().toString())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                    //  putLong() with METADATA_KEY_DATE raises IllegalStateException
                    .putString(MediaMetadataCompat.METADATA_KEY_DATE, DateUtils.dateRFC3339(item.getDescription().getExtras().getLong(Episode.DATE_KEY)))
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, item.getDescription().getMediaUri().toString())
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
        if (rootHints != null && rootHints.containsKey(Podcast.PODCAST_KEY)) {
            return true;
        }
        return false;
    }

    private void showMediaStyleNotification() {
        // Given a media session and its context (usually the component containing the session)
        // Create a NotificationCompat.Builder

        // Get the session's metadata
        MediaControllerCompat controller = session.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Add the metadata for the currently playing track
        builder
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())

                // Enable launching the player by clicking the notification
                .setContentIntent(controller.getSessionActivity())

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                // Be careful about the color
                .setSmallIcon(R.drawable.ic_headset_pink_40dp)
                .setColor(ContextCompat.getColor(this, R.color.secondaryColor))

                // Add a pause button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_pause_white_40dp, getString(R.string.pause_label),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))

                // Take advantage of MediaStyle features
                .setStyle(new MediaStyle()
                        .setMediaSession(session.getSessionToken())
                        .setShowActionsInCompactView(0)

                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_STOP)));

// Display the notification and place the service in the foreground
        startForeground(FOREGROUND_ID, builder.build());
    }
}
