package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.PodcastWatchedEntry;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.EpisodeEntry;

public class UserMetadataDBHelper extends SQLiteOpenHelper {

    /*  This database stores metadata about users navigation in the app. */

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "UserMetaDatabase.db";

    public UserMetadataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.createPodcastWatchedEntry(sqLiteDatabase);
        this.createEpisodeEntry(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        this.dropTable(PodcastWatchedEntry.TABLE_NAME,sqLiteDatabase);
        this.dropTable(EpisodeEntry.TABLE_NAME,sqLiteDatabase);
        this.onCreate(sqLiteDatabase);
    }

    private void createPodcastWatchedEntry(SQLiteDatabase database) {
        String sql = String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", %s BOOLEAN NOT NULL DEFAULT 0, %s INTEGER, %s TEXT NOT NULL," +
                        "FOREIGN KEY(%s) REFERENCES %s(%s));",
                PodcastWatchedEntry.TABLE_NAME,
                PodcastWatchedEntry._ID,
                PodcastWatchedEntry.COLUMN_NAME_STARRED,
                PodcastWatchedEntry.COLUMN_NAME_CURRENT_EPISODE,
                PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID,
                PodcastWatchedEntry.COLUMN_NAME_CURRENT_EPISODE,
                EpisodeEntry.TABLE_NAME,
                EpisodeEntry._ID);
        database.execSQL(sql);
    }

    private void createEpisodeEntry(SQLiteDatabase database){
        String sql = String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", %s INTEGER, %s TEXT, %s INTEGER NOT NULL, %s TEXT NOT NULL," +
                        "FOREIGN KEY(%s) REFERENCES %s(%s));",
                EpisodeEntry.TABLE_NAME,
                EpisodeEntry._ID,
                EpisodeEntry.COLUMN_NAME_CURRENT_PLAYBACK_POSITION,
                EpisodeEntry.COLUMN_NAME_DOWNLOADED_URI,
                EpisodeEntry.COLUMN_NAME_PODCAST,
                PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID,
                EpisodeEntry.COLUMN_NAME_PODCAST,
                PodcastWatchedEntry.TABLE_NAME,
                PodcastWatchedEntry._ID);
        database.execSQL(sql);
    }

    private void dropTable(String tableName, SQLiteDatabase database) {
        String sql = String.format("DROP TABLE IF EXISTS %s", tableName);
        database.execSQL(sql);
    }
}
