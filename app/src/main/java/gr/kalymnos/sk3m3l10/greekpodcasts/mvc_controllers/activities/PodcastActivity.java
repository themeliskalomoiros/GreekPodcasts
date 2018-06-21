package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Toast;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllEpisodesFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.QuickPlayerFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen.PodcastScreenViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen.PodcastScreenViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class PodcastActivity extends AppCompatActivity implements PodcastScreenViewMvc.OnActionPlayClickListener,
        LoaderManager.LoaderCallbacks<String>, AllEpisodesFragment.AllEpisodesFragmentCommunicator {

    private static final String TAG = PodcastActivity.class.getSimpleName();

    private static Podcast cachedPodcast;
    private String cachedPodcasterName;

    private PodcastScreenViewMvc viewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeMvcView();
        setContentView(viewMvc.getRootView());
    }

    private void initializeMvcView() {
        Bundle extras = this.getIntent().getExtras();

        if (extras != null && extras.containsKey(Podcast.PODCAST_KEY)) {
            cachedPodcast = extras.getParcelable(Podcast.PODCAST_KEY);
            if (cachedPodcast != null) {
                this.viewMvc = new PodcastScreenViewMvcImpl(LayoutInflater.from(this),
                        null, this.getSupportFragmentManager(), extras);
                this.viewMvc.setOnActionPlayClickListener(this);
                this.viewMvc.bindPoster(cachedPodcast.getPosterUrl());
                this.viewMvc.bindPodcastTitle(cachedPodcast.getTitle());
                this.viewMvc.bindPodcasterName(cachedPodcast.getTitle());
            } else {
                throw new IllegalStateException(TAG + ": Podcast activity cannot be instantiated with a null Podcast.");
            }
        }
    }

    @Override
    public void onActionPlayClick() {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                if (cachedPodcasterName != null) {
                    this.deliverResult(cachedPodcasterName);
                } else {
                    this.forceLoad();
                }
            }

            @Nullable
            @Override
            public String loadInBackground() {
                //  TODO: Swap with a real service.
                DataRepository webService = new StaticFakeDataRepo();
                return webService.fetchPodcasterName(cachedPodcast.getPodcasterId());
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data != null) {
            this.viewMvc.bindPodcasterName(this.cachedPodcasterName = data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public void onEpisodeClicked(int position) {
        showQuickPlayerFragment();
    }

    @Override
    public void onEpisodePopUpMenuClicked(int position) {

    }

    private void showQuickPlayerFragment() {
        QuickPlayerFragment quickPlayerFragment = new QuickPlayerFragment();
        this.getSupportFragmentManager().beginTransaction()
                .replace(viewMvc.getQuickPlayerContainerId(), quickPlayerFragment)
                .commit();
    }
}
