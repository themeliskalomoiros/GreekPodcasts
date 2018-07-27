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

import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class UploadDataService extends IntentService {

    private static final String ACTION_UPLOAD_AUDIO = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.action.upload_audio";

    private static final String EXTRA_AUDIO_URI = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.audio_uri";
    private static final String EXTRA_PODCAST = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.podcast_push_id";
    private static final String EXTRA_AUDIO_TITLE = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.audio_title";

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

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_AUDIO.equals(action)) {
                final Uri audioUri = intent.getParcelableExtra(EXTRA_AUDIO_URI);
                final Podcast podcast = intent.getParcelableExtra(EXTRA_PODCAST);
                final String audioTitle = intent.getStringExtra(EXTRA_AUDIO_TITLE);
                handleActionUploadAudio(audioTitle, audioUri, podcast);
            }
        }
    }

    private void handleActionUploadAudio(String audioTitle, Uri audioUri, Podcast podcast) {
        StorageReference audioRef = FirebaseStorage.getInstance().getReference()
                .child(ChildNames.EPISODES)
                .child(podcast.getFirebasePushId())
                .child(audioTitle);

        audioRef.putFile(audioUri).addOnSuccessListener(taskSnapshot -> {

            //  TODO:   Show a snackbar indicating that the audio was successfully uploaded
            Toast.makeText(this, audioTitle + " uploaded successfully", Toast.LENGTH_SHORT).show();

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
