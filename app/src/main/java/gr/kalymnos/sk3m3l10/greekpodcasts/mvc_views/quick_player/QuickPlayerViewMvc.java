package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.quick_player;

import android.graphics.Bitmap;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface QuickPlayerViewMvc extends ViewMvc {

    static final int DEFAULT_BACKGROUND_COLOR = R.color.primaryColor;

    interface OnTransportControlsClickListener {
        void onPlayButtonClick();

        void onPauseButtonClick();
    }

    interface OnQuickPlayerClickListener {
        void onQuickPlayerClick();
    }

    void bindEpisodeTitle(String title);

    void bindPodcastTitleColor(int color);

    void bindBackgroundColor(int color);

    void disableRoot(boolean disable);

    Bitmap getPosterBitmap();

    void displayPlayButton(boolean display);

    void bindPodcastPoster(String url);

    void disableTransportControls(boolean disable);

    void setOnTransportControlsClickListener(OnTransportControlsClickListener listener);

    void setOnRootClickListener(OnQuickPlayerClickListener listener);
}
