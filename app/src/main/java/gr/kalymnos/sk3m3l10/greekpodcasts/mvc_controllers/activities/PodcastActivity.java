package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen.PodcastScreenViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class PodcastActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private static final String TAG = PodcastActivity.class.getSimpleName();

    private String cachedPodcasterName;

    private PodcastScreenViewMvc viewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private Podcast getPodcastFromExtras(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null && extras.containsKey(Podcast.PODCAST_KEY)){
            return getIntent().getExtras().getParcelable(Podcast.PODCAST_KEY);
        }
        throw new IllegalStateException(TAG+": Bundle is null or does not contain Podcast.PODCAST_KEY");
    }
}
