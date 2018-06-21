package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_episodes;

import android.support.v4.media.MediaBrowserCompat;

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

    void bindEpisodes(List<MediaBrowserCompat.MediaItem> episodes);

    void displayLoadingIndicator(boolean display);

    void setOnEpisodeClickListener(OnEpisodeClickListener listener);

    void setOnPopUpMenuClickListener(OnPopUpMenuClickListener listener);
}
