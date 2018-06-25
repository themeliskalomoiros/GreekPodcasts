package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract;

public class LocalDatabaseUtils {
    private LocalDatabaseUtils() {

    }

    public static Uri insertEpisode(@NonNull Context context, int currentPlaybackPosition,
                                    String fileUri, @NonNull int podcastLocalDbId, ContentValues values) {
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

    public static int updatePodcastCurrentEpisode(@NonNull Context context, @NonNull int podcastLocalDatabaseId,
                                                  @NonNull ContentValues values) {
        /*  SQL Update Statement
         *
         *   UPDATE table_name
         *   SET column1 - value1, columnN = valueN
         *   WHERE   [condition]
         *
         *
         *   Here the SET role is played by ContentValues*/


        String selection = UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(podcastLocalDatabaseId)};

        return context.getContentResolver()
                .update(UserMetadataContract.PodcastWatchedEntry.CONTENT_URI, values, selection, selectionArgs);
    }
}
