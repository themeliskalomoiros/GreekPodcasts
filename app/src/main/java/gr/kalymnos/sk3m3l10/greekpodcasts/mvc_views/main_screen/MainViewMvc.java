package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen;

import android.support.v7.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface MainViewMvc extends ViewMvc {

    static final int TAB_COUNT = 3;

    interface OnActionCreatePodcastClickListener {
        void onActionCreatePodcastClick();
    }

    Toolbar getToolbar();

    void setOnCreatePodcastClickListener(OnActionCreatePodcastClickListener listener);

    void loadAd(AdRequest adRequest);
}
