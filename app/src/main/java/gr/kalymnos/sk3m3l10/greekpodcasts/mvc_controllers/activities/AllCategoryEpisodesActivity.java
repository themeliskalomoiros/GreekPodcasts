package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllPodcastsFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_category_episodes_screen.AllCategoryEpisodesViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_category_episodes_screen.AllCategoryEpisodesViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.LocalDatabaseTasks;

public class AllCategoryEpisodesActivity extends AppCompatActivity implements AllPodcastsViewMvc.OnPodcastItemClickListener, LoaderManager.LoaderCallbacks<List<Podcast>> {
    private static final int LOADER_ID = 121;
    private static final String TAG = AllCategoryEpisodesActivity.class.getSimpleName();

    private AllCategoryEpisodesViewMvc viewMvc;
    private List<Podcast> cachedPodcasts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getCategoryFromExtras() != null) {
            initializeUi();
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        } else {
            throw new UnsupportedOperationException(TAG + " Category is null");
        }
    }

    private void initializeUi() {
        viewMvc = new AllCategoryEpisodesViewMvcImpl(LayoutInflater.from(this), null);
        viewMvc.setOnPodcastItemClickListener(this);
        setSupportActionBar(viewMvc.getToolbar());
        getSupportActionBar().setTitle(getCategoryFromExtras().getTitle());
        setContentView(viewMvc.getRootView());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cachedPodcasts != null) {
            outState.putParcelableArrayList(Podcast.PODCASTS_KEY, (ArrayList) cachedPodcasts);
        }
    }


    @Override
    public void onItemPodcastClick(int position) {
        if (this.cachedPodcasts != null && this.cachedPodcasts.size() > 0) {

            LocalDatabaseTasks.findPodcastInLocalDatabaseTask(this,
                    cachedPodcasts.get(position),
                    () -> navigateToPodcastActivity(cachedPodcasts.get(position)))
                    .execute();
        }
    }

    private void navigateToPodcastActivity(Podcast podcast) {
        Bundle extras = new Bundle();
        extras.putParcelable(Podcast.PODCAST_KEY, podcast);

        Intent intent = new Intent(this, PodcastActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<List<Podcast>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Podcast>>(this) {

            @Override
            protected void onStartLoading() {
                if (cachedPodcasts != null) {
                    deliverResult(cachedPodcasts);
                } else {
                    viewMvc.displayLoadingIndicator(true);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Podcast> loadInBackground() {
                //  TODO: Replace with a real web service
                DataRepository repo = new StaticFakeDataRepo();
                return repo.fetchPodcastsFromCategory(getCategoryFromExtras().getFirebasePushId());
            }
        };
    }

    private Category getCategoryFromExtras() {
        Category category = getIntent().getParcelableExtra(Category.CATEGORY_KEY);
        return category;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Podcast>> loader, List<Podcast> data) {
        viewMvc.displayLoadingIndicator(false);
        if (data != null && data.size() > 0) {
            viewMvc.bindPodcasts(cachedPodcasts = data);
        } else {
            //  TODO: Pop up a snack bar informing that podcasts could not be fetched
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Podcast>> loader) {

    }
}
