package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.PodcastActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.PodcastsAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.LocalDatabaseTasks;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc.OnPodcastItemClickListener;

public class AllPodcastsFragment extends Fragment implements OnPodcastItemClickListener, LoaderCallbacks<List<Podcast>> {

    protected static final int LOADER_ID = 121;
    private static final String TAG = AllPodcastsFragment.class.getSimpleName();

    protected AllPodcastsViewMvc viewMvc;
    protected List<Podcast> cachedPodcasts = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeViewMvc(inflater, container);
        return this.viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Podcast.PODCASTS_KEY)) {
            this.cachedPodcasts = savedInstanceState.getParcelableArrayList(Podcast.PODCASTS_KEY);
        }
        this.getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.cachedPodcasts != null) {
            outState.putParcelableArrayList(Podcast.PODCASTS_KEY, (ArrayList) cachedPodcasts);
        }
    }

    @Override
    public void onItemPodcastClick(int position) {
        if (this.cachedPodcasts != null && this.cachedPodcasts.size() > 0) {

            LocalDatabaseTasks.findPodcastInLocalDatabaseTask(getActivity(),
                    cachedPodcasts.get(position),
                    () -> navigateToPodcastActivity(cachedPodcasts.get(position)))
            .execute();
        }
    }

    private void navigateToPodcastActivity(Podcast podcast) {
        Bundle extras = new Bundle();
        extras.putParcelable(Podcast.PODCAST_KEY, podcast);

        Intent intent = new Intent(this.getContext(), PodcastActivity.class);
        intent.putExtras(extras);
        this.getContext().startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<List<Podcast>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Podcast>>(this.getContext()) {

            @Override
            protected void onStartLoading() {
                if (cachedPodcasts != null) {
                    this.deliverResult(cachedPodcasts);
                } else {
                    viewMvc.displayLoadingIndicator(true);
                    this.forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Podcast> loadInBackground() {
                //  TODO: Replace with a real web service
                DataRepository repo = new StaticFakeDataRepo();
                return repo.fetchAllPodcasts();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Podcast>> loader, List<Podcast> data) {
        this.viewMvc.displayLoadingIndicator(false);
        if (data != null && data.size() > 0) {
            this.viewMvc.bindPodcasts(cachedPodcasts = data);
        } else {
            //  TODO: Pop up a snack bar informing that podcasts could not be fetched
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Podcast>> loader) {

    }

    private void initializeViewMvc(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.viewMvc = new AllPodcastsViewMvcImpl(inflater, container, new PodcastsAdapter(this.getContext()));
        this.viewMvc.setOnPodcastItemClickListener(this);
    }

}