package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;

public interface AllCategoriesViewMvc extends ViewMvc {

    interface OnCategoryItemClickListener {
        void onCategoryItemClick(int position);
    }

    void bindCategories(List<Category> categories);

    void setOnCategoryItemClickListener(OnCategoryItemClickListener listener);

    void displayLoadingIndicator(boolean display);
}
