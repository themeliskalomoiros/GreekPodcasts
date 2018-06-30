package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.episode_play;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract;

public class DownloadAudioService extends IntentService {
    private static final String ACTION_DOWNLOAD_AUDIO = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.episode_play.action.download_audio";

    // TODO: Rename parameters
    private static final String EXTRA_AUDIO_URL = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.episode_play.extra.audio_url";
    private static final String EXTRA_EPISODE_LOCAL_DB_ID = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.episode_play.extra.extra_episode_database_id";
    private static final String EXTRA_EPISODE_NAME = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.episode_play.extra.extra_episode_name";
    private static final String EXTRA_PODCAST_LOCAL_DB_ID = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.episode_play.extra.extra_podcast_id";
    private static final String TAG = DownloadAudioService.class.getSimpleName();

    public interface OnDownloadAudioFileListener {
        void onDownloadCompleted(String episodeName);

        void onDownloadError(String errorMessage);
    }

    private static OnDownloadAudioFileListener callback;

    public DownloadAudioService() {
        super("DownloadAudioService");
    }

    public static void startActionDownloadAudio(Context context, String audioUrl, int episodeLocalDbId,
                                                String episodeName, int podcastLocalDbId, OnDownloadAudioFileListener listener) {

         callback = listener;

        Intent intent = new Intent(context, DownloadAudioService.class);
        intent.setAction(ACTION_DOWNLOAD_AUDIO);
        intent.putExtra(EXTRA_AUDIO_URL, audioUrl);
        intent.putExtra(EXTRA_EPISODE_LOCAL_DB_ID, episodeLocalDbId);
        intent.putExtra(EXTRA_EPISODE_NAME, episodeName);
        intent.putExtra(EXTRA_PODCAST_LOCAL_DB_ID, podcastLocalDbId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //  Automatically stop's self when this method returns
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_AUDIO.equals(action)) {
                final String audioUrl = intent.getStringExtra(EXTRA_AUDIO_URL);
                final int episodeLocalDbId = intent.getIntExtra(EXTRA_EPISODE_LOCAL_DB_ID, 0);
                final String episodeName = intent.getStringExtra(EXTRA_EPISODE_NAME);
                final int podcastLocalDbId = intent.getIntExtra(EXTRA_PODCAST_LOCAL_DB_ID, 0);
                handleActionDownloadAudio(audioUrl, episodeLocalDbId, episodeName, podcastLocalDbId);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownloadAudio(String audioUrl, int episodeLocalDbId, String episodeName, int podcastLocalDbId) {
        Toast.makeText(this, R.string.downloading_label, Toast.LENGTH_SHORT).show();
        Uri uri = downloadAudioFile(audioUrl, episodeName, podcastLocalDbId);
        if (uri != null) {
            saveUriToDatabase(uri.toString(), episodeLocalDbId, episodeName);
        }
    }

    private void saveUriToDatabase(String uriString, int episodeLocalDbId, String episodeName) {
        ContentValues values = new ContentValues();
        values.put(UserMetadataContract.EpisodeEntry.COLUMN_NAME_DOWNLOADED_URI, uriString);
        Uri episodeUri = UserMetadataContract.EpisodeEntry.CONTENT_URI.buildUpon().appendPath("" + episodeLocalDbId).build();
        getContentResolver().update(episodeUri, values, null, null);
    }

    //  Returns true only if the download was successful
    private Uri downloadAudioFile(String audioUrl, String episodeName, int podcastLocalDbId) {

        try {

            File appDir = getFilesDir();
//            File podcastDir = new File(appDir, String.format("Podcast_%d", podcastLocalDbId));
            File audioFile = new File(appDir, episodeName);

            URL url = new URL(audioUrl);

            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[50];
            int current = 0;

            while ((current = bufferedInputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, current);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(audioFile);
            fileOutputStream.write(buffer.toByteArray());
            fileOutputStream.close();

            callback.onDownloadCompleted(episodeName);

            Uri uri = Uri.fromFile(audioFile);
            return uri;

        } catch (MalformedURLException e) {
            Log.d(TAG,e.getMessage());
            callback.onDownloadError(e.getMessage());
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
            callback.onDownloadError(e.getMessage());
        }

        return null;
    }
}
