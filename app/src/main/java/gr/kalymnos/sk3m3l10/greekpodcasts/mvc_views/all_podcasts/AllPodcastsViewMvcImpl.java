package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class AllPodcastsViewMvcImpl implements AllPodcastsViewMvc {

    public static final int NUMBER_COLUMNS_ON_PORTRAIT = 2;
    public static final int NUMBER_COLUMNS_ON_LAND = 3;


    protected View rootView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private boolean onLand;

    protected PodcastsAdapter adapter;

    private OnPodcastItemClickListener onPodcastItemClickListener;

    public AllPodcastsViewMvcImpl(LayoutInflater inflater, ViewGroup parent, @NonNull PodcastsAdapter adapter) {
        this.rootView = inflater.inflate(R.layout.podcasts_list, parent, false);
        this.adapter = adapter;
        initialize();
    }


    @Override
    public void bindPodcasts(List<Podcast> podcasts) {
        this.adapter.addPodcasts(podcasts);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void setOnPodcastItemClickListener(OnPodcastItemClickListener listener) {
        this.adapter.setOnPodcastItemClickListener(listener);
    }

    @Override
    public void displayLoadingIndicator(boolean display) {
        if (display) {
            this.progressBar.setVisibility(View.VISIBLE);
        } else {
            this.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View getRootView() {
        return this.rootView;
    }

    protected void initialize() {
        this.progressBar = this.rootView.findViewById(R.id.pb_loading_indicator);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        this.recyclerView = this.rootView.findViewById(R.id.recycler_view);
        if (this.recyclerView == null) {
            //  We are not on portrait
            this.recyclerView = this.rootView.findViewById(R.id.recycler_view_land);
            if (this.recyclerView != null) {
                //  We are on land mode
                this.onLand = true;
            }
        }

        GridLayoutManager gridLayoutManager;
        if (this.onLand) {
            gridLayoutManager = new GridLayoutManager(this.getRootView().getContext(), NUMBER_COLUMNS_ON_LAND);
        } else {
            gridLayoutManager = new GridLayoutManager(this.getRootView().getContext(), NUMBER_COLUMNS_ON_PORTRAIT);
        }

        this.recyclerView.setLayoutManager(gridLayoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.adapter);
    }
}
