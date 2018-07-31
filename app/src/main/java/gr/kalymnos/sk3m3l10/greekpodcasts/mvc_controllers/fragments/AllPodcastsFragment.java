package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.PodcastActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.PodcastsAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.LocalDatabaseTasks;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc.OnPodcastItemClickListener;

public class AllPodcastsFragment extends Fragment implements OnPodcastItemClickListener {

    private static final String TAG = AllPodcastsFragment.class.getSimpleName();

    private AllPodcastsViewMvc viewMvc;
    private List<Podcast> cachedPodcasts = null;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference allPodcastsRef;
    private ChildEventListener allPodcastsRefChildEventListener;

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.cachedPodcasts != null) {
            outState.putParcelableArrayList(Podcast.PODCASTS_KEY, (ArrayList) cachedPodcasts);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(allPodcastsRef!=null){
            allPodcastsRef.removeEventListener(allPodcastsRefChildEventListener);
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
        allPodcastsRef = firebaseDatabase.getReference().child(ChildNames.PODCASTS);
        allPodcastsRefChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (cachedPodcasts == null) {
                    cachedPodcasts = new ArrayList<>();
                }

                Podcast podcast = dataSnapshot.getValue(Podcast.class);
                if (podcast != null) {
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
        };
        allPodcastsRef.addChildEventListener(allPodcastsRefChildEventListener);
    }
}