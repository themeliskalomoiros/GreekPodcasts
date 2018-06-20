package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface PodcastScreenViewMvc extends ViewMvc {

    public static final int TAB_COUNT = 2;

    interface OnActionPlayClickListener {

        void onActionPlayClick();
    }

    void bindPoster(String url);

    void bindPodcastTitle(String title);

    void bindPodcasterName(String name);

    void setOnActionPlayClickListener(OnActionPlayClickListener listener);

    int getQuickPlayerContainerId();

    int getMainContentContainerId();

    int getMainContentContainerCurrentItem();

    int getMainContentCurrentItemPosition();

    Toolbar getToolbar();
}
