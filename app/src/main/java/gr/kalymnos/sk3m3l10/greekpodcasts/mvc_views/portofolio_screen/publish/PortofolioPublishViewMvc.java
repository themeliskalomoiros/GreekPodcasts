package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish;

import android.graphics.Bitmap;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public interface PortofolioPublishViewMvc extends ViewMvc {

    interface OnButtonsClickListener {

        void onEditPodcastClick(Podcast podcast);

        void onEditDescriptionClick(String description);

        void onViewEpisodesClick();

        void onAddEpisodeClick();

        void onPosterClick();
    }

    interface OnSpinnerChosenListener {

        void onPodcastChosen(int position);

        void onCategoryChosen(int position);
    }

    void bindEpisodes(List<Episode> episodes);

    void bindPoster(Bitmap poster);

    void bindDescription(String description);

    void setOnButtonsClickListener(OnButtonsClickListener listener);

    void setOnSpinnerChosenListener(OnSpinnerChosenListener listener);

    boolean onLand();

    void bindTitle(String title);
}
