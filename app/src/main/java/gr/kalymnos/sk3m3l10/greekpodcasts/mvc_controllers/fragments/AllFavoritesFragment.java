package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites.AllFavoritesViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites.FavoritesAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

import static gr.kalymnos.sk3m3l10.greekpodcasts.local_database.UserMetadataContract.PodcastWatchedEntry;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites.AllFavoritesViewMvc.OnPodcastItemLongClickListener;

public class AllFavoritesFragment extends AllPodcastsFragment implements OnPodcastItemLongClickListener{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.viewMvc = new AllFavoritesViewMvcImpl(inflater, container, new FavoritesAdapter(this.getContext()));
        this.viewMvc.setOnPodcastItemClickListener(this);
        ((AllFavoritesViewMvcImpl)this.viewMvc).setOnPodcastItemLongClickListener(this);
        this.getLoaderManager().restartLoader(LOADER_ID, null, this);
        return this.viewMvc.getRootView();
    }

    @NonNull
    @Override
    public Loader<List<Podcast>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Podcast>>(this.getContext()) {

            final Uri starredUri = PodcastWatchedEntry.CONTENT_URI;
            DataRepository webService;

            @Override
            protected void onStartLoading() {
                if (cachedPodcasts != null) {
                    this.deliverResult(cachedPodcasts);
                } else {
                    viewMvc.displayLoadingIndicator(true);
                    this.forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Podcast> loadInBackground() {
                //  First fetch starred (favorites) podcasts from local database.
                String selection = PodcastWatchedEntry.COLUMN_NAME_STARRED + ">?";
                String[] selectionArgs = {"0"};
                Cursor starredPodcastsCursor = getContext().getContentResolver().query(starredUri,
                        null, selection, selectionArgs, null);


                //  TODO: Replace with a real web service
                this.webService = new StaticFakeDataRepo();
                cachedPodcasts = this.webService.fetchStarredPodcasts(starredPodcastsCursor);

                if (starredPodcastsCursor != null)
                    starredPodcastsCursor.close();

                return cachedPodcasts;
            }
        };
    }

    @Override
    public void onPodcastItemLongClick(int position) {
        //  TODO: Do something when a starred podcast is long clicked.
        Toast.makeText(this.getContext(), "Podcast long clicked.", Toast.LENGTH_SHORT).show();
    }
}