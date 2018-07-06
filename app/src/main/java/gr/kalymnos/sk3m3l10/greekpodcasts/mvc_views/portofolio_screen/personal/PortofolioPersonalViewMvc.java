package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal;

import android.graphics.Bitmap;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public interface PortofolioPersonalViewMvc extends ViewMvc {

    interface OnButtonsClickListener {

        void onEditPodcasterName(String name);

        void onEditPersonalStatementClick(String statement);

        void onEditPromotionLinkClick();
    }

    void bindPodcasterName(String name);

    void bindPodcastPoster(String url);

    void bindPromotionLinks(List<PromotionLink> promotionLinks);

    void setOnButtonsClickListener(OnButtonsClickListener listener);

    void displayLoadingIndicator(boolean display);

    void bindPersonalStatement(String statement);
}
