package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_category_episodes_screen;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class AllCategoryEpisodesViewMvcImpl implements AllCategoryEpisodesViewMvc {

    private View rootView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public AllCategoryEpisodesViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.all_category_episodes, parent, false);
        toolbar = rootView.findViewById(R.id.toolbar);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        progressBar = rootView.findViewById(R.id.pb_loading_indicator);
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
