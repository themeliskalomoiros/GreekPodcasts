package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.create;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface PortofolioCreateViewMvc extends ViewMvc {

    int getCreateProfileFirstMessage();

    int getCompleteAllFieldsMessage();

    interface OnPosterClickListener {
        void onPosterClick();
    }

    interface OnCategorySelectedListener {
        void onCategoryChosen(int position);

        void onNothingChosen();
    }

    void bindPoster(Uri uri);

    int getPosterContainerHeight();

    int getPosterContainerWidth();

    void displayLoadingIndicator(boolean display);

    void addCategoriesToSpinner(String[] titles);

    void setOnPosterClickListener(OnPosterClickListener listener);

    void setOnCategorySelectedListener(OnCategorySelectedListener listener);

    String getTitleText();

    String getDescriptionText();

    ImageView getPosterImageView();

    int getSelectedCategoryPosition();

    void displayImageHint(boolean display);

    void displayImageFileName(boolean display);

    void bindImageFileName(String fileName);
}
