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
                                    String downloadUrl, @NonNull int podcastLocalDbId, ContentValues values) {
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
}
