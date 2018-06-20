package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public interface AllPodcastsViewMvc extends ViewMvc {

    interface OnPodcastItemClickListener {
        void onItemPodcastClick(int position);
    }

    void bindPodcasts(List<Podcast> podcasts);

    void setOnPodcastItemClickListener(OnPodcastItemClickListener listener);

    void displayLoadingIndicator(boolean display);
}
