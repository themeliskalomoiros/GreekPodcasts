package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal;

import android.net.Uri;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public interface PortofolioPersonalViewMvc extends ViewMvc {

    interface OnViewsClickListener {

        void onEditPodcasterName();

        void onEditPersonalStatementClick();

        void onEditPromotionLinkClick();

        void onImageClick();


    }
    void bindImage(Uri uri);
    void bindPodcasterName(String name);

    void bindImage(String url);

    void bindPromotionLinks(List<PromotionLink> promotionLinks);

    void setOnViewsClickListener(OnViewsClickListener listener);

    void displayLoadingIndicator(boolean display);

    void bindPersonalStatement(String statement);

    void displayImageHint(boolean display);

    void displayImageFileName(boolean display);

    void bindImageFileName(String fileName);

    int getNameDialogTitleRes();

    int getPersonalStatementDialogTitleRes();
}
