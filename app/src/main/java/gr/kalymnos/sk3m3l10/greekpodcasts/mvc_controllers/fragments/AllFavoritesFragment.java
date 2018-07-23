package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
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
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites.AllFavoritesViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites.AllFavoritesViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites.FavoritesAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.PodcastWatchedEntry;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites.AllFavoritesViewMvc.OnPodcastItemLongClickListener;

public class AllFavoritesFragment extends Fragment implements AllPodcastsViewMvc.OnPodcastItemClickListener, OnPodcastItemLongClickListener, LoaderManager.LoaderCallbacks<List<String>> {

    private static final int LOADER_ID = 123;
    private List<String> cachedFavoritePodcastsPushIds;
    private List<Podcast> cachedPodcasts;

    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference allPodcastsRef;

    private AllFavoritesViewMvc viewMvc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeViewMvc(inflater, container);
        return viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Podcast.PODCASTS_KEY)) {
            cachedPodcasts = savedInstanceState.getParcelableArrayList(Podcast.PODCASTS_KEY);
            viewMvc.bindPodcasts(cachedPodcasts);
        } else {
            initializeFirebase();
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.cachedPodcasts != null) {
            outState.putParcelableArrayList(Podcast.PODCASTS_KEY, (ArrayList) cachedPodcasts);
        }
    }

    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<String>>(getContext()) {

            @Override
            protected void onStartLoading() {
                if (cachedFavoritePodcastsPushIds != null && cachedFavoritePodcastsPushIds.size() > 0) {
                    deliverResult(cachedFavoritePodcastsPushIds);
                } else {
                    viewMvc.displayLoadingIndicator(true);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<String> loadInBackground() {

                List<String> pushIds = new ArrayList<>();
                Cursor favoritesCursor = getFavoritePodcastsCursor();

                if (favoritesCursor != null) {
                    if (favoritesCursor.getCount() > 0) {
                        while (favoritesCursor.moveToNext()) {
                            int pushIdIndex = favoritesCursor.getColumnIndex(PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID);
                            pushIds.add(favoritesCursor.getString(pushIdIndex));
                        }
                    }
                    favoritesCursor.close();
                }

                return pushIds;
            }

            private Cursor getFavoritePodcastsCursor() {
                //  First fetch starred (favorites) podcasts from local database.
                //  Zero '0' means not starred, non zero means starred
                String selection = PodcastWatchedEntry.COLUMN_NAME_STARRED + ">?";
                String[] selectionArgs = {"0"};

                Uri starredUri = PodcastWatchedEntry.CONTENT_URI;

                return getContext().getContentResolver().query(starredUri,
                        null, selection, selectionArgs, null);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
        if (data != null && data.size() > 0) {
            //  We got the push ids of the favorite podcasts
            cachedFavoritePodcastsPushIds = data;
            loadFavoritePodcastsFromFirebase();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }

    private void loadFavoritePodcastsFromFirebase() {
        allPodcastsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewMvc.displayLoadingIndicator(false);

                List<Podcast> tempList = new ArrayList<>();
                for (DataSnapshot podcastSnapshot : dataSnapshot.getChildren()) {
                    Podcast podcast = podcastSnapshot.getValue(Podcast.class);
                    podcast.setFirebasePushId(podcastSnapshot.getKey());

                    if (isPodcastFavorite(podcast)) {
                        tempList.add(podcast);
                    }
                }

                if (tempList.size() > 0) {
                    cachedPodcasts = tempList;
                    viewMvc.bindPodcasts(cachedPodcasts);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private boolean isPodcastFavorite(Podcast podcast) {
                for (String pushId : cachedFavoritePodcastsPushIds) {
                    if (pushId.equals(podcast.getFirebasePushId())) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onItemPodcastClick(int position) {
        //  TODO: Do something when a starred podcast is clicked.
    }

    @Override
    public void onPodcastItemLongClick(int position) {
        //  TODO: Do something when a starred podcast is long clicked.

    }

    private void initializeViewMvc(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        viewMvc = new AllFavoritesViewMvcImpl(inflater, container, new FavoritesAdapter(getContext()));
        viewMvc.setOnPodcastItemClickListener(this);
        viewMvc.setOnPodcastItemLongClickListener(this);
    }

    private void initializeFirebase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            allPodcastsRef = firebaseDatabase.getReference().child(ChildNames.CHILD_NAME_PODCASTS);
        }
    }
}