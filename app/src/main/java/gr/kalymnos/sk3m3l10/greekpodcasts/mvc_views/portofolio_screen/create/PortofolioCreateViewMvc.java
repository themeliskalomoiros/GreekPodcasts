package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.create;

import android.graphics.Bitmap;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface PortofolioCreateViewMvc extends ViewMvc {

    interface OnPosterClickListener {
        void onPosterClick();
    }

    void bindPoster(Bitmap poster);

    void setOnPosterClickListener(OnPosterClickListener listener);
}
