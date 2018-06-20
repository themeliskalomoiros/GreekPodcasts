package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories.AllCategoriesViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;

public class AllCategoriesViewMvcImpl implements AllCategoriesViewMvc {

    private View rootView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private boolean onLand;

    private CategoriesAdapter adapter;

    public AllCategoriesViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        this.rootView = inflater.inflate(R.layout.categories_list, parent, false);
        initialize();
    }

    @Override
    public void bindCategories(List<Category> categories) {
        this.adapter.addCategories(categories);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void setOnCategoryItemClickListener(OnCategoryItemClickListener listener) {
        this.adapter.setOnCategoryItemClickListener(listener);
    }

    @Override
    public void displayLoadingIndicator(boolean display) {
        if (display) {
            this.progressBar.setVisibility(View.VISIBLE);
        } else {
            this.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    private void initialize() {
        this.progressBar = this.rootView.findViewById(R.id.pb_loading_indicator);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        this.recyclerView = this.rootView.findViewById(R.id.recycler_view);
        if (this.recyclerView == null) {
            //  We are not on portrait
            this.recyclerView = this.rootView.findViewById(R.id.recycler_view_land);
            if (this.recyclerView != null) {
                //  We are on land mode
                this.onLand = true;
            }
        }

        LinearLayoutManager linearLayoutManager;
        if (this.onLand) {
            linearLayoutManager = new LinearLayoutManager(this.getRootView().getContext(), LinearLayoutManager.HORIZONTAL, false);
        } else {
            linearLayoutManager = new LinearLayoutManager(this.getRootView().getContext(), LinearLayoutManager.VERTICAL, false);
        }

        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setHasFixedSize(true);

        this.adapter = new CategoriesAdapter(this.getRootView().getContext());
        this.recyclerView.setAdapter(this.adapter);
    }
}
