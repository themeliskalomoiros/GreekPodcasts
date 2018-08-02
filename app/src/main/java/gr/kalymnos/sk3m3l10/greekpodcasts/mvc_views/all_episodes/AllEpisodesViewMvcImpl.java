package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_episodes;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;

public class AllEpisodesViewMvcImpl implements AllEpisodesViewMvc {

    private View rootView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private EpisodesAdapter adapter;

    public AllEpisodesViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        this.rootView = inflater.inflate(R.layout.episodes_list, parent, false);
        this.progressBar = this.rootView.findViewById(R.id.pb_loading_indicator);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        this.recyclerView = this.rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.rootView.getContext());
        this.adapter = new EpisodesAdapter(this.rootView.getContext());
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setHasFixedSize(true);
    }

    @Override
    public int getMenuClickedMessage() {
        return R.string.menu_clicked_message;
    }

    @Override
    public void markSelectedPosition(int position) {
        if (adapter != null) {
            if (adapter.cachedViewHolder!=null){
                adapter.cachedViewHolder.markSelectionView(position);
            }
        }
    }

    @Override
    public int getItemPositionFromMediaId(String mediaId) {
        if (adapter != null) {
            return adapter.getItemPositionFromMediaId(mediaId);
        } else {
            throw new UnsupportedOperationException(AllEpisodesViewMvcImpl.class.getSimpleName() + ": Can't get item position from media id because adapter is null.");
        }
    }

    @Override
    public void bindEpisodes(List<MediaBrowserCompat.MediaItem> episodes) {
        this.adapter.addEpisodes(episodes);
        this.adapter.notifyDataSetChanged();
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
    public void setOnEpisodeClickListener(OnEpisodeClickListener listener) {
        this.adapter.setOnEpisodeClickListener(listener);
    }

    @Override
    public void setOnPopUpMenuClickListener(OnPopUpMenuClickListener listener) {
        this.adapter.setOnPopUpMenuClickListener(listener);
    }

    @Override
    public String getErrorFetchingEpisodesMessage() {
        return rootView.getContext().getString(R.string.error_fetching_episodes_message);
    }

    @Override
    public View getRootView() {
        return this.rootView;
    }
}
