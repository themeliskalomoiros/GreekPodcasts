package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;

public interface AllFavoritesViewMvc extends AllPodcastsViewMvc {

    interface OnPodcastItemLongClickListener {
        void onPodcastItemLongClick(int position);
    }

    void setOnPodcastItemLongClickListener(OnPodcastItemLongClickListener listener);
}
