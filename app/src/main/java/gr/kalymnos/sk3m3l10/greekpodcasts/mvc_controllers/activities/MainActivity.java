package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Toast;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen.MainViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen.MainViewMvcImpl;

public class MainActivity extends AppCompatActivity implements MainViewMvc.OnActionCreatePodcastClickListener {

    private MainViewMvc viewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViewMvc();

        //  TODO:   Shouldn't do something here with checking savedInstanceState and fragments? Check it!!!
        this.setContentView(this.viewMvc.getRootView());
    }

    @Override
    public void onActionCreatePodcastClick() {
        //  TODO:   Implement the create podcast action
//        Cursor cursor = getContentResolver().query(UserMetadataContract.PodcastWatchedEntry.CONTENT_URI,
//                null,UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_STARRED+"!=?",new String[]{"0"},null);
//
        Cursor cursor = getContentResolver().query(UserMetadataContract.PodcastWatchedEntry.CONTENT_URI,
                null,null,null,null);

        while (cursor.moveToNext()){
            int idIndex = cursor.getColumnIndex(UserMetadataContract.PodcastWatchedEntry._ID);
            int starredIndex = cursor.getColumnIndex(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_STARRED);
            int curEpisodeIndex = cursor.getColumnIndex(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_CURRENT_EPISODE);
            int pushIdIndex = cursor.getColumnIndex(UserMetadataContract.PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID);
            String s = String.format("Podcast with id:%d, current episode:%d, push id:\"%s\" and starred:%d",cursor.getInt(idIndex),cursor.getInt(curEpisodeIndex),cursor.getString(pushIdIndex),cursor.getInt(starredIndex));
            Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViewMvc() {
        this.viewMvc = new MainViewMvcImpl(LayoutInflater.from(this), null, this.getSupportFragmentManager());
        this.viewMvc.setOnCreatePodcastClickListener(this);
        this.setSupportActionBar(this.viewMvc.getToolbar());
    }
}
