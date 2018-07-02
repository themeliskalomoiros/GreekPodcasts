package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen;

import android.support.v7.widget.Toolbar;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface PortofolioViewMvc extends ViewMvc {

    static final int TAB_COUNT = 3;

    interface OnActionSaveClickListener {
        void onSaveClick();
    }

    Toolbar getToolbar();

    void setOnActionSaveClickListener(OnActionSaveClickListener listener);
}
