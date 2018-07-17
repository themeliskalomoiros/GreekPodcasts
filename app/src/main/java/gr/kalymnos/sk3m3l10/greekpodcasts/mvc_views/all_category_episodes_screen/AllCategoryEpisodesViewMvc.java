package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_category_episodes_screen;

import android.support.v7.widget.Toolbar;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface AllCategoryEpisodesViewMvc extends ViewMvc {

    Toolbar getToolbar();

    void displayLoadingIndicator(boolean display);
}
