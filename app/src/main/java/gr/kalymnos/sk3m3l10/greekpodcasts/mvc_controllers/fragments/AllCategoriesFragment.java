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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.AllCategoryEpisodesActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories.AllCategoriesViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories.AllCategoriesViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories.AllCategoriesViewMvc.OnCategoryItemClickListener;

public class AllCategoriesFragment extends Fragment implements OnCategoryItemClickListener {

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
        } else {
            fetchAndBindCategories();
        }
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

    private void fetchAndBindCategories() {
        viewMvc.displayLoadingIndicator(true);
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child(ChildNames.CATEGORIES);
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewMvc.displayLoadingIndicator(false);
                
                List<Category> tempCategoryList = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        tempCategoryList.add(category);
                    }
                }

                if (tempCategoryList.size() > 0) {
                    cachedCategories = tempCategoryList;
                    viewMvc.bindCategories(cachedCategories);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeViewMvc(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.viewMvc = new AllCategoriesViewMvcImpl(inflater, container);
        this.viewMvc.setOnCategoryItemClickListener(this);
    }
}