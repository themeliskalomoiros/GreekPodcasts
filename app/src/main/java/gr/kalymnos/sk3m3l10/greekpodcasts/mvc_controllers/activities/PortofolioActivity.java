package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.SaveOperationer;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.ViewAllEpisodesFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.PortofolioViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.PortofolioViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.FragmentUtils;

public class PortofolioActivity extends AppCompatActivity implements PortofolioViewMvc.OnActionSaveClickListener, ViewAllEpisodesFragment.OnAddButtonClickListener {

    private static final String TAG = PortofolioActivity.class.getSimpleName();
    private PortofolioViewMvc viewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewMvc = new PortofolioViewMvcImpl(LayoutInflater.from(this), null, this.getSupportFragmentManager());
        viewMvc.setOnActionSaveClickListener(this);
        setSupportActionBar(viewMvc.getToolbar());
        setContentView(viewMvc.getRootView());
    }


    @Override
    public void onSaveClick() {
        String currentFragmentTag = FragmentUtils.fragmentTag(viewMvc.getViewPagerId(), viewMvc.getCurrentTabItem());
        if (!TextUtils.isEmpty(currentFragmentTag)) {
            SaveOperationer currentFragment = (SaveOperationer) getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
            if (currentFragment != null) {
                currentFragment.save();
            } else {
                throw new UnsupportedOperationException(TAG + ": all PortofolioActivity's fragments must implement " + SaveOperationer.class.getSimpleName());
            }
        }
    }

    @Override
    public void onViewAllEpisodesFragmentAddButtonClicked(Podcast podcast) {
        Intent intent = new Intent(this, AddEpisodeActivity.class);
        intent.putExtra(Podcast.PODCAST_KEY, podcast);
        startActivity(intent);
    }
}
