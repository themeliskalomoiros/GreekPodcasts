package gr.kalymnos.sk3m3l10.greekpodcasts.local_database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class UserMetadataContract {

    private UserMetadataContract() {
    }

    //  Content Provider's authority (its identity).
    public static final String AUTHORITY = "gr.kalymnos.sk3m3l10.greekpodcasts.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_PODCAST_WATCHED = PodcastWatchedEntry.TABLE_NAME;
    public static final String PATH_EPISODE = EpisodeEntry.TABLE_NAME;

    public static class PodcastWatchedEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PODCAST_WATCHED)
                .build();

        //  Table and Column names.
        public static final String TABLE_NAME = "podcast_watched";
        public static final String COLUMN_NAME_STARRED = "starred";
        public static final String COLUMN_NAME_CURRENT_EPISODE = "current_episode";
        public static final String COLUMN_NAME_FIREBASE_PUSH_ID = "firebase_push_id";
    }

    public static class EpisodeEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EPISODE)
                .build();

        //  Table and Column names.
        public static final String TABLE_NAME = "episode";
        public static final String COLUMN_NAME_CURRENT_PLAYBACK_POSITION = "current_playback_position";
        public static final String COLUMN_NAME_DOWNLOADED_URI = "downloaded_uri";
        public static final String COLUMN_NAME_PODCAST = "podcast";
        public static final String COLUMN_NAME_FIREBASE_PUSH_ID = "firebase_push_id";
    }
}
