package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish;

import android.graphics.Bitmap;
import android.net.Uri;

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

    void bindPoster(Uri uri);

    void bindDescription(String description);

    boolean onLand();

    void addPodcastsToSpinner(String[] titles);

    void addCategoriesToSpinner(String[] titles);

    void displayPodcastLoadingIndicator(boolean display);

    void displayCategoryLoadingIndicator(boolean display);

    void displayEpisodesLoadingIndicator(boolean display);

    int getSelectedPodcastPosition();

    int getSelectedCategoryPosition();

    void setOnButtonsClickListener(OnButtonsClickListener listener);

    void setOnPodcastSelectedListener(OnPodcastSelectedListener listener);

    void setOnCategorySelectedListener(OnCategorySelectedListener listener);

    void setOnItemsSelectedListener(OnItemsSelectedListener listener);

    void setCategorySelection(int position);

    int getAllEpisodesContainerId();

    int getTitleDialogTitleRes();

    int getDescriptionDialogTitleRes();

    void displayImageHint(boolean display);

    void displayImageFileName(boolean display);

    int getPosterContainerWidth();

    int getPosterContainerHeight();

    byte[] getPosterData();

    String getPodcastTitle();

    String getDescription();

    boolean posterExists();

    void bindImageFileName(String name);

    void selectPodcastSpinnerItem(int position);
}
