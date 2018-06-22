package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.episode_screen;

import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface EpisodeViewMvc extends ViewMvc {

    interface OnActionButtonsClickListener {
        void onStarClick();

        void onDownloadClick();

        void onInfoClick();
    }

    interface OnTransportControlsClickListener {
        void onPlayButtonClick();

        void onPauseButtonClick();

        void onSkipToNextButtonClick();

        void onSkipToPreviousButtonClick();
    }

    interface OnPodcasterClickListener{
        void onPodcasterClick();
    }

    void bindPoster(String url);

    void bindPodcaster(String name);

    void bindEpisodeTitle(String title);

    void bindPlaybackPosition(String position);

    void bindPlaybackDuration(String duration);

    void displayPlayButton(boolean display);

    void enableSeekBar(boolean enable);

    void bindSeekBarMax(int max);

    void disableTransportControls(boolean disable);

    void bindSeekBarProgress(int progress);

    void setSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener);

    Toolbar getToolbar();

    void setOnEpisodeControlsClickListener(OnActionButtonsClickListener listener);

    void setOnTransportControlsClickListener(OnTransportControlsClickListener listener);

    void setOnPodcasterClickListener(OnPodcasterClickListener listener);
}
