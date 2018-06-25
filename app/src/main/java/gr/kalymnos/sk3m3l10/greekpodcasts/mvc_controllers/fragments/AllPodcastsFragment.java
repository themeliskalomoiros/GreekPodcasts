package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.PodcastWatchedEntry;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.PodcastActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.PodcastsAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc.OnPodcastItemClickListener;

public class AllPodcastsFragment extends Fragment implements OnPodcastItemClickListener, LoaderCallbacks<List<Podcast>> {

    protected static final int LOADER_ID = 121;
    private static final String TAG = AllPodcastsFragment.class.getSimpleName();

    protected AllPodcastsViewMvc viewMvc;
    protected List<Podcast> cachedPodcasts = null;

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
        }
        this.getLoaderManager().restartLoader(LOADER_ID, null, this);
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

            DatabaseOperations.findPodcastInLocalDatabaseTask(getActivity(),
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

    @NonNull
    @Override
    public Loader<List<Podcast>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Podcast>>(this.getContext()) {

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
                //  TODO: Replace with a real web service
                DataRepository repo = new StaticFakeDataRepo();
                return repo.fetchAllPodcasts();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Podcast>> loader, List<Podcast> data) {
        this.viewMvc.displayLoadingIndicator(false);
        if (data != null && data.size() > 0) {
            this.viewMvc.bindPodcasts(cachedPodcasts = data);
        } else {
            //  TODO: Pop up a snack bar informing that podcasts could not be fetched
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Podcast>> loader) {

    }

    private void initializeViewMvc(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.viewMvc = new AllPodcastsViewMvcImpl(inflater, container, new PodcastsAdapter(this.getContext()));
        this.viewMvc.setOnPodcastItemClickListener(this);
    }

    private static class DatabaseOperations {

        private static final int EXACTLY_ONE_PODCAST = 1;
        private static final int INVALIDE_EPISODE_ID = -1;

        static AsyncTask<Void, Void, Boolean> findPodcastInLocalDatabaseTask(@NonNull Activity activity, @NonNull Podcast podcast,
                                                                             Runnable action) {
            return new AsyncTask<Void, Void, Boolean>() {

                private int _id;

                @Override
                protected Boolean doInBackground(Void... voids) {
                    String selection = PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID + "= ?";
                    String[] selectionArgs = new String[]{podcast.getFirebasePushId()};
                    Cursor cursor = activity.getContentResolver().query(PodcastWatchedEntry.CONTENT_URI,
                            null, selection, selectionArgs, null);

                    if (cursor != null) {
                        if (cursor.getCount() == EXACTLY_ONE_PODCAST) {
                            cursor.moveToFirst();
                            int idColumnIndex = cursor.getColumnIndex(PodcastWatchedEntry._ID);
                            _id = cursor.getInt(idColumnIndex);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        throw new UnsupportedOperationException(TAG + ": Cursor should not be null");
                    }
                }

                @Override
                protected void onPostExecute(Boolean podcastExists) {
                    if (podcastExists) {
                        //  First set the local db id so other activities may use it.
                        //  We don't want to query this id all the time
                        podcast.setLocalDbId(_id);
                        activity.runOnUiThread(action);
                    } else {
                        //  Insert the podcast and execute the same action (navigate to another activity)
                        insertPodcastTask(activity, PodcastWatchedEntry.CONTENT_URI,
                                DatabaseOperations.contentValuesToInsertPodcast(false,
                                        INVALIDE_EPISODE_ID,
                                        podcast.getFirebasePushId()),
                                podcast,
                                action);
                    }
                }
            };
        }

        static AsyncTask<Void, Void, Integer> insertPodcastTask(@NonNull Activity activity, @NonNull Uri contentUri,
                                                                @NonNull ContentValues values, Podcast podcast, Runnable action) {
            return new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    Uri uri = activity.getContentResolver().insert(contentUri, values);
                    //  return the _id from uri
                    return Integer.parseInt(uri.getPathSegments().get(1));
                }

                @Override
                protected void onPostExecute(Integer _id) {
                    //  First set the podcasts id and then run the action
                    podcast.setLocalDbId(_id);
                    activity.runOnUiThread(action);
                }
            };
        }

        static ContentValues contentValuesToInsertPodcast(boolean starred, int currentEpisodeId, String firebasePushId) {
            ContentValues values = new ContentValues();
            values.put(PodcastWatchedEntry.COLUMN_NAME_STARRED, starred);
            values.put(PodcastWatchedEntry.COLUMN_NAME_CURRENT_EPISODE, currentEpisodeId);
            values.put(PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID, firebasePushId);
            return values;
        }

    }
}