package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen;

import android.support.v7.widget.Toolbar;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public interface PodcasterViewMvc extends ViewMvc {

    interface OnPromotionLinkClickListener {
        void onPromotionLinkClick(int position);
    }

    void bindPodcasterName(String name);

    void bindJoinedDate(String date);

    void bindPersonalStatement(String personalStatement);

    void bindPodcasterImageUrl(String url);

    Toolbar getToolBar();

    void displayLoading(boolean display);

    void bindPromotionLinks(List<PromotionLink> promotionLinks);

    void setOnPromotionLinkClickListener(OnPromotionLinkClickListener listener);

}
