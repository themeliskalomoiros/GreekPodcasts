package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.add_episode.AddEpisodeViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.add_episode.AddEpisodeViewMvcImpl;

public class AddEpisodeActivity extends AppCompatActivity implements AddEpisodeViewMvc.OnActionsClickListener {

    private AddEpisodeViewMvc viewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewMvc = new AddEpisodeViewMvcImpl(LayoutInflater.from(this), null);
        viewMvc.setOnActionsClickListener(this);
        setContentView(viewMvc.getRootView());
    }

    @Override
    public void onInsertAudioClick() {

    }

    @Override
    public void onUploadActionClick() {

    }
}
