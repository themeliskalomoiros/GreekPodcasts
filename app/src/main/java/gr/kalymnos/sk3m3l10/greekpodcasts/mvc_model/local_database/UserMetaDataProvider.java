package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.EpisodeEntry;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.PodcastWatchedEntry;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.AUTHORITY;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.PATH_EPISODE;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.PATH_PODCAST_WATCHED;

public class UserMetaDataProvider extends ContentProvider {

    private UserMetadataDBHelper dbHelper;

    public static final int PODCASTS = 100;
    public static final int PODCASTS_WITH_ID = 101;
    public static final int EPISODES = 200;
    public static final int EPISODES_WITH_ID = 201;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        this.dbHelper = new UserMetadataDBHelper(this.getContext());
        //  This method is done, so lets return true...
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        final SQLiteDatabase sqLiteDatabase = this.dbHelper.getReadableDatabase();
        int matchedUri = uriMatcher.match(uri);

        Cursor returnedCursor = null;
        switch (matchedUri) {
            case PODCASTS_WITH_ID:
                String podcastIdString = uri.getPathSegments().get(1);
                String pSelection = "_id=?";
                String[] pSelectionArgs = new String[]{podcastIdString};

                returnedCursor = sqLiteDatabase.query(PodcastWatchedEntry.TABLE_NAME, columns, pSelection, pSelectionArgs,
                        null, null, null);
                break;
            case EPISODES_WITH_ID:
                String episodeIdString = uri.getPathSegments().get(1);
                String eSelection = "_id=?";
                String[] eSelectionArgs = new String[]{episodeIdString};

                returnedCursor = sqLiteDatabase.query(EpisodeEntry.TABLE_NAME, columns, eSelection, eSelectionArgs,
                        null, null, null);
                break;
            case PODCASTS:
                returnedCursor = sqLiteDatabase.query(PodcastWatchedEntry.TABLE_NAME, columns, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EPISODES:
                returnedCursor = sqLiteDatabase.query(EpisodeEntry.TABLE_NAME, columns, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Could not get a cursor for " + uri);
        }

        //  Set a notification Uri on the cursor to listen for changes
        returnedCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnedCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // No need to close the sqLiteDatabase because
        // https://stackoverflow.com/questions/8531201/when-should-i-call-close-on-sqliteopenhelper-used-by-contentprovider
        final SQLiteDatabase sqLiteDatabase = this.dbHelper.getWritableDatabase();

        int matchedUri = uriMatcher.match(uri);

        Uri returnedUri = null;
        long insertedId;

        switch (matchedUri) {
            case PODCASTS:

                insertedId = sqLiteDatabase.insert(PodcastWatchedEntry.TABLE_NAME, null, contentValues);
                if (insertedId > 0) {
                    // Insertion completed successfully.
                    returnedUri = ContentUris.withAppendedId(PodcastWatchedEntry.CONTENT_URI, insertedId);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;

            case EPISODES:

                insertedId = sqLiteDatabase.insert(EpisodeEntry.TABLE_NAME, null, contentValues);
                if (insertedId > 0) {
                    // Insertion completed successfully.
                    returnedUri = ContentUris.withAppendedId(EpisodeEntry.CONTENT_URI, insertedId);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = this.dbHelper.getWritableDatabase();
        int matchedUri = uriMatcher.match(uri);

        int numberOfRowsUpdated = 0;
        switch (matchedUri) {
            case PODCASTS_WITH_ID:

                String podcastId = uri.getPathSegments().get(1);
                numberOfRowsUpdated = sqLiteDatabase.update(PodcastWatchedEntry.TABLE_NAME, contentValues,
                        "_id=?", new String[]{podcastId});
                break;

            case EPISODES_WITH_ID:
                String episodeId = uri.getPathSegments().get(1);
                numberOfRowsUpdated = sqLiteDatabase.update(EpisodeEntry.TABLE_NAME, contentValues,
                        "_id=?", new String[]{episodeId});
                break;
            default:
                throw new UnsupportedOperationException("Could not get a cursor for " + uri);
        }
        return numberOfRowsUpdated;
    }

    public static UriMatcher buildUriMatcher() {
        //  This UriMatcher will associate URIs with their int match.
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, PATH_PODCAST_WATCHED, PODCASTS);
        matcher.addURI(AUTHORITY, PATH_EPISODE, EPISODES);
        matcher.addURI(AUTHORITY, PATH_PODCAST_WATCHED + "/#", PODCASTS_WITH_ID);
        matcher.addURI(AUTHORITY, PATH_EPISODE + "/#", EPISODES_WITH_ID);
        return matcher;
    }
}
