package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_category_episodes_screen;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.PodcastsAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class AllCategoryEpisodesViewMvcImpl implements AllCategoryEpisodesViewMvc {

    private View rootView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private PodcastsAdapter adapter;

    public AllCategoryEpisodesViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.all_category_episodes, parent, false);
        toolbar = rootView.findViewById(R.id.toolbar);
        progressBar = rootView.findViewById(R.id.pb_loading_indicator);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        adapter = new PodcastsAdapter(rootView.getContext());
        int columns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(rootView.getContext(), columns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void bindPodcasts(List<Podcast> podcasts) {
        adapter.addPodcasts(podcasts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setOnPodcastItemClickListener(AllPodcastsViewMvc.OnPodcastItemClickListener listener) {
        adapter.setOnPodcastItemClickListener(listener);
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void displayLoadingIndicator(boolean display) {
        if (display) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
