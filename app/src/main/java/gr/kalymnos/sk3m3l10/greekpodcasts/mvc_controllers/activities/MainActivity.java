package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.io.File;

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
        Cursor cursor = getContentResolver().query(UserMetadataContract.EpisodeEntry.CONTENT_URI,
                null,null,null,null);

        while (cursor.moveToNext()){
            int idIndex = cursor.getColumnIndex(UserMetadataContract.EpisodeEntry._ID);
            int uriIndex = cursor.getColumnIndex(UserMetadataContract.EpisodeEntry.COLUMN_NAME_DOWNLOADED_URI);
            String string = String.format("Episode with id %d and uri %s",cursor.getInt(idIndex),cursor.getString(uriIndex));
            Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void initializeViewMvc() {
        this.viewMvc = new MainViewMvcImpl(LayoutInflater.from(this), null, this.getSupportFragmentManager());
        this.viewMvc.setOnCreatePodcastClickListener(this);
        this.setSupportActionBar(this.viewMvc.getToolbar());
    }
}
