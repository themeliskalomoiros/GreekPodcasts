package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen.PodcastScreenViewMvc.TAB_COUNT;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AboutPodcastFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllEpisodesFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen.MainPagerAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;

public class PodcastPagerAdapter extends MainPagerAdapter {

    private Bundle args;
    private static final String TAG = PodcastPagerAdapter.class.getSimpleName();

    public PodcastPagerAdapter(FragmentManager fm, @NonNull Bundle pagerArguments, @NonNull String... titles) {
        super(fm, titles);
        args = pagerArguments;
    }

    public Fragment[] getFragmentsArray() {
        if (fragmentsArray == null) {
            fragmentsArray = new Fragment[TAB_COUNT];

            fragmentsArray[0] = new AllEpisodesFragment();
            fragmentsArray[0].setArguments(args);

            fragmentsArray[1] = new AboutPodcastFragment();
            fragmentsArray[1].setArguments(getAboutPodcastArgs());
        }
        return fragmentsArray;
    }

    private Bundle getAboutPodcastArgs() {
        Podcast podcast = args.getParcelable(Podcast.PODCAST_KEY);
        if (podcast != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Podcast.DESCRIPTION_KEY, podcast.getDescription());
            return bundle;
        } else {
            throw new UnsupportedOperationException(TAG + ": Podcast should not be null");
        }
    }
}
