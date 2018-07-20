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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.PodcastActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.PodcastsAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.LocalDatabaseTasks;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc.OnPodcastItemClickListener;

public class AllPodcastsFragment extends Fragment implements OnPodcastItemClickListener {

    private static final String TAG = AllPodcastsFragment.class.getSimpleName();

    protected AllPodcastsViewMvc viewMvc;
    protected List<Podcast> cachedPodcasts = null;

    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference allPodcastsRef;
    private ChildEventListener allPodcastsChildEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeViewMvc(inflater, container);
        return this.viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Podcast.PODCASTS_KEY)) {
            this.cachedPodcasts = savedInstanceState.getParcelableArrayList(Podcast.PODCASTS_KEY);
            viewMvc.bindPodcasts(cachedPodcasts);
        } else {
            initializeFirebase();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (allPodcastsRef != null) {
            allPodcastsRef.addChildEventListener(allPodcastsChildEventListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (allPodcastsRef != null) {
            allPodcastsRef.removeEventListener(allPodcastsChildEventListener);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.cachedPodcasts != null) {
            outState.putParcelableArrayList(Podcast.PODCASTS_KEY, (ArrayList) cachedPodcasts);
        }
    }

    @Override
    public void onItemPodcastClick(int position) {
        if (this.cachedPodcasts != null && this.cachedPodcasts.size() > 0) {

            LocalDatabaseTasks.findPodcastInLocalDatabaseTask(getActivity(),
                    cachedPodcasts.get(position),
                    () -> navigateToPodcastActivity(cachedPodcasts.get(position)))
                    .execute();
        }
    }

    private void navigateToPodcastActivity(Podcast podcast) {
        Bundle extras = new Bundle();
        extras.putParcelable(Podcast.PODCAST_KEY, podcast);

        Intent intent = new Intent(this.getContext(), PodcastActivity.class);
        intent.putExtras(extras);
        this.getContext().startActivity(intent);
    }

    private void initializeViewMvc(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.viewMvc = new AllPodcastsViewMvcImpl(inflater, container, new PodcastsAdapter(this.getContext()));
        this.viewMvc.setOnPodcastItemClickListener(this);
    }

    private void initializeFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        allPodcastsRef = firebaseDatabase.getReference().child(ChildNames.CHILD_NAME_PODCASTS);
        bindPodcastsToUi();
        allPodcastsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (isCachedPodcastsListValid()) {
                    Podcast podcast = dataSnapshot.getValue(Podcast.class);
                    //  Always setting the firebasePushId of the podcast because it's used in local database
                    podcast.setFirebasePushId(dataSnapshot.getKey());
                    cachedPodcasts.add(podcast);
                    viewMvc.bindPodcasts(cachedPodcasts);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private boolean isCachedPodcastsListValid() {
                return cachedPodcasts != null && cachedPodcasts.size() > 0;
            }
        };
    }

    private void bindPodcastsToUi() {
        viewMvc.displayLoadingIndicator(true);

        allPodcastsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot podcastListSnapshot) {
                viewMvc.displayLoadingIndicator(false);
                List<Podcast> tempList = new ArrayList<>();
                for (DataSnapshot podcastSnapshot : podcastListSnapshot.getChildren()) {
                    Podcast podcast = podcastSnapshot.getValue(Podcast.class);
                    //  Always setting the firebasePushId of the podcast because it's used in local database
                    podcast.setFirebasePushId(podcastSnapshot.getKey());
                    tempList.add(podcast);
                }
                if (tempList != null && tempList.size() > 0) {
                    cachedPodcasts = tempList;
                    viewMvc.bindPodcasts(cachedPodcasts);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  TODO: Either this listener failed at the server or Firebase rules security, do something...
            }
        });
    }
}