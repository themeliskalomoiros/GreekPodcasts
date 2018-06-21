package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_episodes;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;

public interface AllEpisodesViewMvc extends ViewMvc {

    interface OnEpisodeClickListener {
        void onEpisodeClick(int position);
    }

    interface OnPopUpMenuClickListener {
        void onPopUpMenuClick(int position);
    }

    void markSelectedPosition(int position);

    void bindEpisodes(List<Episode> episodes);

    void displayLoadingIndicator(boolean display);

    void setOnEpisodeClickListener(OnEpisodeClickListener listener);

    void setOnPopUpMenuClickListener(OnPopUpMenuClickListener listener);
}
