package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class UploadDataService extends IntentService {

    private static final String ACTION_UPLOAD_AUDIO = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.action.upload_audio";
    private static final String ACTION_UPDATE_PODCAST = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.action.update_podcast";

    private static final String EXTRA_AUDIO_URI = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.audio_uri";
    private static final String EXTRA_PODCAST = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.podcast_push_id";
    private static final String EXTRA_AUDIO_TITLE = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.audio_title";
    private static final String EXTRA_PODCAST_TITLE = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.PODCAST_title";
    private static final String EXTRA_PODCAST_DESCRIPTION = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.PODCAST_description";
    private static final String EXTRA_PODCAST_POSTER_DATA = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.PODCAST_poster_data";
    private static final String EXTRA_PODCAST_PUSH_ID = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.PODCAST_push_id";
    private static final String EXTRA_CATEGORY_PUSH_ID = "gr.kalymnos.sk3m3l10.greekCATEGORYs.mvc_controllers.fragments.portofolio.extra.CATEGORY_push_id";

    public UploadDataService() {
        super("UploadDataService");
    }

    public static void startActionUploadAudio(Context context, String audioTitle, Uri audioUri, Podcast podcast) {
        Intent intent = new Intent(context, UploadDataService.class);
        intent.setAction(ACTION_UPLOAD_AUDIO);
        intent.putExtra(EXTRA_AUDIO_URI, audioUri);
        intent.putExtra(EXTRA_PODCAST, podcast);
        intent.putExtra(EXTRA_AUDIO_TITLE, audioTitle);
        context.startService(intent);
    }

    public static void startActionUpdatePodcast(Context context, String podcastTitle, String description,
                                                byte[] posterData, String categoryPushId, String podcastPushId) {
        Intent intent = new Intent(context, UploadDataService.class);
        intent.setAction(ACTION_UPDATE_PODCAST);
        intent.putExtra(EXTRA_PODCAST_TITLE, podcastTitle);
        intent.putExtra(EXTRA_PODCAST_DESCRIPTION, description);
        intent.putExtra(EXTRA_PODCAST_POSTER_DATA, posterData);
        intent.putExtra(EXTRA_PODCAST_PUSH_ID, podcastPushId);
        intent.putExtra(EXTRA_CATEGORY_PUSH_ID, categoryPushId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_UPLOAD_AUDIO)) {
                final Uri audioUri = intent.getParcelableExtra(EXTRA_AUDIO_URI);
                final Podcast podcast = intent.getParcelableExtra(EXTRA_PODCAST);
                final String audioTitle = intent.getStringExtra(EXTRA_AUDIO_TITLE);
                handleActionUploadAudio(audioTitle, audioUri, podcast);
            } else if (action.equals(ACTION_UPDATE_PODCAST)) {
                final String podcastTitle = intent.getStringExtra(EXTRA_PODCAST_TITLE);
                final String podcastDescription = intent.getStringExtra(EXTRA_PODCAST_DESCRIPTION);
                final byte[] posterData = intent.getByteArrayExtra(EXTRA_PODCAST_POSTER_DATA);
                final String podcastPushId = intent.getStringExtra(EXTRA_PODCAST_PUSH_ID);
                final String categoryPushId = intent.getStringExtra(EXTRA_CATEGORY_PUSH_ID);
                handleActionUpdatePodcast(podcastTitle, podcastDescription, posterData, categoryPushId, podcastPushId);
            }
        }
    }

    private void handleActionUpdatePodcast(String podcastTitle, String description, byte[] posterData,
                                           String categoryPushId, String podcastPushId) {
        getPosterStorageReference(podcastPushId).putBytes(posterData).addOnSuccessListener(taskSnapshot -> {

            getPodcastDatabaseReference(podcastPushId).child(Podcast.FIELD_NAME_TITLE).setValue(podcastTitle);
            getPodcastDatabaseReference(podcastPushId).child(Podcast.FIELD_NAME_DESCRIPTION).setValue(description);
            getPodcastDatabaseReference(podcastPushId).child(Podcast.FIELD_NAME_POSTER_URL).setValue(taskSnapshot.getDownloadUrl().toString());
            getPodcastDatabaseReference(podcastPushId).child(Podcast.FIELD_NAME_CATEGORY_ID).setValue(categoryPushId);

        }).addOnFailureListener(exception -> {
            //  TODO: Switch with snackbar
            Toast.makeText(this, getString(R.string.could_not_upload_message) + exception.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private StorageReference getPosterStorageReference(String podcastPushId) {
        return FirebaseStorage.getInstance().getReference()
                .child(ChildNames.PODCASTS).child(podcastPushId).child(ChildNames.POSTER);
    }

    private DatabaseReference getPodcastDatabaseReference(String podcastPushId) {
        return FirebaseDatabase.getInstance().getReference().child(ChildNames.PODCASTS).child(podcastPushId);
    }

    private void handleActionUploadAudio(String audioTitle, Uri audioUri, Podcast podcast) {
        StorageReference audioRef = FirebaseStorage.getInstance().getReference()
                .child(ChildNames.EPISODES)
                .child(podcast.getFirebasePushId())
                .child(audioTitle);

        audioRef.putFile(audioUri).addOnSuccessListener(taskSnapshot -> {

            //  TODO:   Show a snackbar indicating that the audio was successfully uploaded
            Toast.makeText(this, audioTitle + getString(R.string.uploaded_successfully_message), Toast.LENGTH_SHORT).show();

            uploadEpisode(audioTitle, podcast, taskSnapshot);

        }).addOnFailureListener(exception ->
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void uploadEpisode(String audioTitle, Podcast podcast, UploadTask.TaskSnapshot taskSnapshot) {
        Episode episode = new Episode();
        episode.setTitle(audioTitle);
        episode.setDateMilli(System.currentTimeMillis());
        episode.setUrl(taskSnapshot.getDownloadUrl().toString());

        DatabaseReference episodeRef = FirebaseDatabase.getInstance().getReference()
                .child(ChildNames.EPISODES)
                .child(podcast.getFirebasePushId())
                .push();

        episodeRef.setValue(episode);
    }
}
