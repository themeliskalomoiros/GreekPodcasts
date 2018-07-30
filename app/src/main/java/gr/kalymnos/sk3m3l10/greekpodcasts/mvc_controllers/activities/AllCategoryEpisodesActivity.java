package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllPodcastsFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_category_episodes_screen.AllCategoryEpisodesViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_category_episodes_screen.AllCategoryEpisodesViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.LocalDatabaseTasks;

public class AllCategoryEpisodesActivity extends AppCompatActivity implements AllPodcastsViewMvc.OnPodcastItemClickListener {
    private static final String TAG = AllCategoryEpisodesActivity.class.getSimpleName();

    private AllCategoryEpisodesViewMvc viewMvc;
    private List<Podcast> cachedPodcasts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getCategoryFromExtras() != null) {
            initializeUi();

            if (savedInstanceState != null && savedInstanceState.containsKey(Podcast.PODCASTS_KEY)) {
                cachedPodcasts = savedInstanceState.getParcelableArrayList(Podcast.PODCASTS_KEY);
                viewMvc.bindPodcasts(cachedPodcasts);
            } else {
                fetchAndBindCategoryPodcasts();
            }
        } else {
            throw new UnsupportedOperationException(TAG + " Category is null");
        }
    }

    private void initializeUi() {
        viewMvc = new AllCategoryEpisodesViewMvcImpl(LayoutInflater.from(this), null);
        viewMvc.setOnPodcastItemClickListener(this);
        setSupportActionBar(viewMvc.getToolbar());
        getSupportActionBar().setTitle(getCategoryFromExtras().getTitle());
        setContentView(viewMvc.getRootView());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cachedPodcasts != null) {
            outState.putParcelableArrayList(Podcast.PODCASTS_KEY, (ArrayList) cachedPodcasts);
        }
    }


    @Override
    public void onItemPodcastClick(int position) {
        if (this.cachedPodcasts != null && this.cachedPodcasts.size() > 0) {

            LocalDatabaseTasks.findPodcastInLocalDatabaseTask(this,
                    cachedPodcasts.get(position),
                    () -> navigateToPodcastActivity(cachedPodcasts.get(position)))
                    .execute();
        }
    }

    private void fetchAndBindCategoryPodcasts() {
        viewMvc.displayLoadingIndicator(true);
        FirebaseDatabase.getInstance().getReference().child(ChildNames.PODCASTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewMvc.displayLoadingIndicator(false);

                List<Podcast> tempPodcastList = new ArrayList<>();
                for (DataSnapshot podcastSnapshot : dataSnapshot.getChildren()) {
                    Podcast podcast = podcastSnapshot.getValue(Podcast.class);
                    if (podcast != null) {
                        podcast.setFirebasePushId(podcastSnapshot.getKey());
                        if (podcast.getCategoryId().equals(getCategoryFromExtras().getFirebasePushId())) {
                            //  Add the podcast only if it belongs to this Category
                            tempPodcastList.add(podcast);
                        }
                    }
                }

                if (tempPodcastList.size() > 0) {
                    cachedPodcasts = tempPodcastList;
                    viewMvc.bindPodcasts(cachedPodcasts);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void navigateToPodcastActivity(Podcast podcast) {
        Bundle extras = new Bundle();
        extras.putParcelable(Podcast.PODCAST_KEY, podcast);

        Intent intent = new Intent(this, PodcastActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }

    private Category getCategoryFromExtras() {
        Category category = getIntent().getParcelableExtra(Category.CATEGORY_KEY);
        return category;
    }
}
