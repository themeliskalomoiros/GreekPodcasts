package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.AllCategoryEpisodesActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories.AllCategoriesViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories.AllCategoriesViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories.AllCategoriesViewMvc.OnCategoryItemClickListener;

public class AllCategoriesFragment extends Fragment implements OnCategoryItemClickListener, LoaderCallbacks<List<Category>> {

    private static final int LOADER_ID = 121;
    private List<Category> cachedCategories = null;

    private AllCategoriesViewMvc viewMvc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeViewMvc(inflater, container);
        return this.viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Category.CATEGORIES_KEY)) {
            this.cachedCategories = savedInstanceState.getParcelableArrayList(Category.CATEGORIES_KEY);
        }
        this.getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.cachedCategories != null) {
            outState.putParcelableArrayList(Category.CATEGORIES_KEY, (ArrayList) this.cachedCategories);
        }
    }

    @Override
    public void onCategoryItemClick(int position) {
        if (getAllCategoryEpisodesActivityIntent(position) != null) {
            getContext().startActivity(getAllCategoryEpisodesActivityIntent(position));
        }
    }

    @NonNull
    private Intent getAllCategoryEpisodesActivityIntent(int position) {
        if (cachedCategories != null) {
            Intent intent = new Intent(getContext(), AllCategoryEpisodesActivity.class);
            intent.putExtra(Category.CATEGORY_KEY, cachedCategories.get(position));
            return intent;
        }

        return null;
    }

    @NonNull
    @Override
    public Loader<List<Category>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Category>>(this.getContext()) {

            @Override
            protected void onStartLoading() {
                if (cachedCategories != null) {
                    this.deliverResult(cachedCategories);
                } else {
                    viewMvc.displayLoadingIndicator(true);
                    this.forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Category> loadInBackground() {
                //  TODO: Replace with real service.
                DataRepository repo = new StaticFakeDataRepo();
                return repo.fetchAllCategories();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Category>> loader, List<Category> data) {
        this.viewMvc.displayLoadingIndicator(false);
        if (data != null && data.size() > 0) {
            this.viewMvc.bindCategories(cachedCategories = data);
        } else {
            //  TODO: Pop up a snack bar informing that categories could not be fetched
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Category>> loader) {

    }

    private void initializeViewMvc(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.viewMvc = new AllCategoriesViewMvcImpl(inflater, container);
        this.viewMvc.setOnCategoryItemClickListener(this);
    }
}