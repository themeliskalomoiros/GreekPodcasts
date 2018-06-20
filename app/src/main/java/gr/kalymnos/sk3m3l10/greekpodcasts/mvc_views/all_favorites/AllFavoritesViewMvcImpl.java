package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.PodcastsAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class AllFavoritesViewMvcImpl extends AllPodcastsViewMvcImpl implements AllFavoritesViewMvc{

    public AllFavoritesViewMvcImpl(LayoutInflater inflater, ViewGroup parent, @NonNull FavoritesAdapter adapter) {
        super(inflater, parent, adapter);
    }

    @Override
    public void setOnPodcastItemLongClickListener(OnPodcastItemLongClickListener listener) {
        ((FavoritesAdapter)this.adapter).setOnPodcastItemLongClickListener(listener);
    }
}
