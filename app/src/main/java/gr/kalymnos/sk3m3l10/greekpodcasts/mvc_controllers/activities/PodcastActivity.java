package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllEpisodesFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.QuickPlayerFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen.PodcastScreenViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen.PodcastScreenViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;

public class PodcastActivity extends AppCompatActivity implements PodcastScreenViewMvc.OnActionPlayClickListener,
        AllEpisodesFragment.AllEpisodesFragmentCommunicator {

    private static final String TAG = PodcastActivity.class.getSimpleName();

    private Podcast cachedPodcast;
    private String cachedPodcasterName;

    private PodcastScreenViewMvc viewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeMvcView();
        if (savedInstanceState != null && savedInstanceState.containsKey(Podcaster.NAME_KEY)) {
            cachedPodcasterName = savedInstanceState.getString(Podcaster.NAME_KEY);
            viewMvc.bindPodcasterName(cachedPodcasterName);
        }else{
            fetchPodcasterName();
        }
        setContentView(viewMvc.getRootView());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(cachedPodcasterName)) {
            outState.putString(Podcaster.NAME_KEY, cachedPodcasterName);
        }
    }

    @Override
    public void onActionPlayClick() {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEpisodeClicked(int position) {
        showQuickPlayerFragment();
    }

    @Override
    public void onEpisodePopUpMenuClicked(int position) {

    }

    private void showQuickPlayerFragment() {
        Bundle args = new Bundle();
        args.putString(Podcaster.PUSH_ID_KEY, cachedPodcast.getPodcasterId());
        args.putString(Podcast.PODCAST_KEY, cachedPodcast.getFirebasePushId());
        args.putInt(Podcast.LOCAL_DB_ID_KEY, cachedPodcast.getLocalDbId());
        args.putString(Podcast.DESCRIPTION_KEY, cachedPodcast.getDescription());

        QuickPlayerFragment quickPlayerFragment = new QuickPlayerFragment();
        quickPlayerFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(viewMvc.getQuickPlayerContainerId(), quickPlayerFragment)
                .commit();
    }

    private void initializeMvcView() {
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(Podcast.PODCAST_KEY)) {
            cachedPodcast = extras.getParcelable(Podcast.PODCAST_KEY);
            if (cachedPodcast != null) {
                viewMvc = new PodcastScreenViewMvcImpl(LayoutInflater.from(this),
                        null, getSupportFragmentManager(), extras);
                viewMvc.setOnActionPlayClickListener(this);
                viewMvc.bindPoster(cachedPodcast.getPosterUrl());
                viewMvc.bindPodcastTitle(cachedPodcast.getTitle());
            } else {
                throw new IllegalStateException(TAG + ": Podcast activity cannot be instantiated with a null Podcast.");
            }
        }
    }

    private void fetchPodcasterName() {
        FirebaseDatabase.getInstance().getReference()
                .child(ChildNames.PODCASTERS)
                .child(cachedPodcast.getPodcasterId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Podcaster podcaster = dataSnapshot.getValue(Podcaster.class);
                        if (podcaster!=null){
                            viewMvc.bindPodcasterName(cachedPodcasterName = podcaster.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
