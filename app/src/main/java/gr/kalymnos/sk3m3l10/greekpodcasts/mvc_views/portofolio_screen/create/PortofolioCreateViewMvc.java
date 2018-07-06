package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.create;

import android.graphics.Bitmap;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface PortofolioCreateViewMvc extends ViewMvc {

    interface OnPosterClickListener {
        void onPosterClick();
    }

    interface OnCategorySelectedListener {
        void onCategoryChosen(int position);

        void onNothingChosen();
    }

    void bindPoster(Bitmap poster);

    int getPosterContainerHeight();

    int getPosterContainerWidth();

    void displayLoadingIndicator(boolean display);

    void addCategoriesToSpinner(String[] titles);

    void setOnPosterClickListener(OnPosterClickListener listener);

    void setOnCategorySelectedListener(OnCategorySelectedListener listener);

    String getTitleText();

    String getDescriptionText();
}
