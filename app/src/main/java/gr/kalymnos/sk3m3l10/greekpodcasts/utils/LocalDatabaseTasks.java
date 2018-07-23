package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllPodcastsFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class LocalDatabaseTasks {

    private static final int EXACTLY_ONE_PODCAST = 1;
    private static final int FIRST_EPISODE_ID = 1;
    private static final String TAG = LocalDatabaseTasks.class.getSimpleName();

    public static AsyncTask<Void, Void, Boolean> findPodcastInLocalDatabaseTask(@NonNull Activity activity, @NonNull Podcast podcast,
                                                                                Runnable action) {
        return new AsyncTask<Void, Void, Boolean>() {

            private int _id;

            @Override
            protected Boolean doInBackground(Void... voids) {
                String selection = UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID + "= ?";
                String[] selectionArgs = new String[]{podcast.getFirebasePushId()};
                Cursor cursor = activity.getContentResolver().query(UserMetadataContract.PodcastWatchedEntry.CONTENT_URI,
                        null, selection, selectionArgs, null);

                if (cursor != null) {
                    if (cursor.getCount() == EXACTLY_ONE_PODCAST) {
                        cursor.moveToFirst();
                        int idColumnIndex = cursor.getColumnIndex(UserMetadataContract.PodcastWatchedEntry._ID);
                        _id = cursor.getInt(idColumnIndex);
                        cursor.close();
                        return true;
                    } else {
                        cursor.close();
                        return false;
                    }
                } else {
                    throw new UnsupportedOperationException(TAG + ": Cursor should not be null");
                }
            }

            @Override
            protected void onPostExecute(Boolean podcastExists) {
                if (podcastExists) {
                    //  First set the local db id so other activities may use it.
                    //  We don't want to query this id all the time
                    podcast.setLocalDbId(_id);
                    activity.runOnUiThread(action);
                } else {
                    //  Insert the podcast and execute the same action (navigate to another activity)
                    insertPodcastTask(activity, UserMetadataContract.PodcastWatchedEntry.CONTENT_URI,
                            LocalDatabaseTasks.contentValuesToInsertPodcast(false,
                                    FIRST_EPISODE_ID,
                                    podcast.getFirebasePushId()),
                            podcast,
                            action).execute();
                }
            }
        };
    }

    public static AsyncTask<Void, Void, Integer> insertPodcastTask(@NonNull Activity activity, @NonNull Uri contentUri,
                                                                   @NonNull ContentValues values, Podcast podcast, Runnable action) {
        return new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                Uri uri = activity.getContentResolver().insert(contentUri, values);
                //  return the _id from uri
                return Integer.parseInt(uri.getPathSegments().get(1));
            }

            @Override
            protected void onPostExecute(Integer _id) {
                //  First set the podcasts id and then run the action
                podcast.setLocalDbId(_id);
                activity.runOnUiThread(action);
            }
        };
    }

    public static ContentValues contentValuesToInsertPodcast(boolean starred, int currentEpisodeId, String firebasePushId) {
        ContentValues values = new ContentValues();
        if (starred) {
            values.put(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_STARRED, 1);
        } else {
            values.put(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_STARRED, 0);
        }
        values.put(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_CURRENT_EPISODE, currentEpisodeId);
        values.put(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID, firebasePushId);
        return values;
    }
}
