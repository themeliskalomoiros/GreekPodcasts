package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish;

import android.graphics.Bitmap;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public interface PortofolioPublishViewMvc extends ViewMvc {

    interface OnButtonsClickListener {

        void onEditPodcastClick(int itemPosition);

        void onEditDescriptionClick();

        void onViewEpisodesClick();

        void onAddEpisodeClick();

        void onPosterClick();
    }

    interface OnPodcastSelectedListener {

        void onPodcastSelected(int position);
    }

    interface OnCategorySelectedListener {

        void onCategorySelected(int position);
    }

    interface OnItemsSelectedListener {

        void onPodcastSelected(int position);

        void onCategorySelected(int position);
    }

    void bindEpisodes(List<Episode> episodes);

    void bindPoster(String url);

    void bindDescription(String description);

    boolean onLand();

    void addPodcastsToSpinner(String[] titles);

    void addCategoriesToSpinner(String[] titles);

    void bindTitle(String title);

    void displayPodcastLoadingIndicator(boolean display);

    void displayCategoryLoadingIndicator(boolean display);

    void displayEpisodesLoadingIndicator(boolean display);

    int getSelectedPodcastPosition();

    void setOnButtonsClickListener(OnButtonsClickListener listener);

    void setOnPodcastSelectedListener(OnPodcastSelectedListener listener);

    void setOnCategorySelectedListener(OnCategorySelectedListener listener);

    void setOnItemsSelectedListener(OnItemsSelectedListener listener);

    void setCategorySelection(int position);
}
