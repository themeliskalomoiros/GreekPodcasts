package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.episode_play.EpisodePlayActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract;

public class LocalDatabaseUtils {


    public static final int ONE_EPISODE = 1;
    public static final int INVALID_ID = -1;
    private static final String TAG = LocalDatabaseUtils.class.getSimpleName();


    private LocalDatabaseUtils() {

    }

    public static Cursor queryPodcast(@NonNull Context context, @NonNull int podcastLocalDbId) {

        Uri contentUriWithAppendedId = UserMetadataContract.PodcastWatchedEntry.CONTENT_URI
                .buildUpon()
                .appendPath(String.valueOf(podcastLocalDbId))
                .build();

        return context.getContentResolver().query(contentUriWithAppendedId,
                null,
                null,
                null,
                null);
    }

    public static Uri insertEpisode(@NonNull Context context, ContentValues values) {
        return context.getContentResolver().insert(UserMetadataContract.EpisodeEntry.CONTENT_URI, values);
    }

    public static Cursor queryEpisode(@NonNull Context context, @NonNull String episodePushId, @NonNull int podcastLocalDbId) {
        String selection = UserMetadataContract.EpisodeEntry.COLUMN_NAME_FIREBASE_PUSH_ID + " = ? AND "
                + UserMetadataContract.EpisodeEntry.COLUMN_NAME_PODCAST + " = ?";
        String[] selectionArgs = new String[]{episodePushId, String.valueOf(podcastLocalDbId)};

        return context.getContentResolver().query(UserMetadataContract.EpisodeEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
    }

    public static Cursor queryEpisode(@NonNull Context context, @NonNull int episodeLocalDbId, @NonNull int podcastLocalDbId) {
        String selection = UserMetadataContract.EpisodeEntry._ID+ " = ? AND "
                + UserMetadataContract.EpisodeEntry.COLUMN_NAME_PODCAST + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(episodeLocalDbId), String.valueOf(podcastLocalDbId)};

        return context.getContentResolver().query(UserMetadataContract.EpisodeEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
    }

    public static int updatePodcastTask(@NonNull Context context, @NonNull int podcastLocalDatabaseId,
                                        @NonNull ContentValues values) {
        /*  SQL Update Statement
         *
         *   UPDATE table_name
         *   SET column1 - value1, columnN = valueN
         *   WHERE   [condition]
         *
         *
         *   Here the SET role is played by ContentValues*/


        Uri contentUriWithAppendedId = UserMetadataContract.PodcastWatchedEntry.CONTENT_URI
                .buildUpon()
                .appendEncodedPath(String.valueOf(podcastLocalDatabaseId))
                .build();

        return context.getContentResolver()
                .update(contentUriWithAppendedId, values, null, null);
    }

    public static AsyncTask<Void, Void, Cursor> isPodcastStarredTask(@NonNull Activity activity, int podcastLocalDatabaseId, Runnable drawStarAction, Runnable undrawStarAction) {
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

    public static AsyncTask<Void, Void, Cursor> clickPodcastTask(@NonNull Activity activity, int podcastLocalDatabaseId, Runnable starPodcastAction, Runnable unStarPodcastAction) {
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

    public static AsyncTask<Void, Void, Integer> starPodcastTask(@NonNull Activity activity, int podcastLocalDatabaseId,
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

    public static AsyncTask<Void, Void, Boolean> isDownloadedTask(@NonNull Activity activity, @NonNull String episodePushId, int podcastLocalDbId,
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

    public static AsyncTask<Void, Void, Integer> cacheCurrentEpisodeLocalDbIdTask(@NonNull Activity activity,
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
