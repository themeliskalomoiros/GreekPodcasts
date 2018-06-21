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

    private Bundle allEpisodesArgs, aboutPodcastArgs;

    public PodcastPagerAdapter(FragmentManager fm, @NonNull Bundle pagerArguments, @NonNull String... titles) {
        super(fm, titles);
        initializeFragmentsArgs(pagerArguments);
    }

    public Fragment[] getFragmentsArray() {
        if (fragmentsArray == null) {
            fragmentsArray = new Fragment[TAB_COUNT];

            fragmentsArray[0] = new AllEpisodesFragment();
            fragmentsArray[0].setArguments(allEpisodesArgs);

            fragmentsArray[1] = new AboutPodcastFragment();
            fragmentsArray[1].setArguments(aboutPodcastArgs);
        }
        return fragmentsArray;
    }

    private void initializeFragmentsArgs(@NonNull Bundle pagerArguments) {
        if (pagerArguments!=null && pagerArguments.containsKey(Podcast.PODCAST_KEY)){

            Podcast podcast = pagerArguments.getParcelable(Podcast.PODCAST_KEY);

            String posterUrl = podcast.getPosterUrl();
            String podcasterId = podcast.getPodcasterId();
            String episodesId = podcast.getEpisodesId();
            allEpisodesArgs = new Bundle();
            allEpisodesArgs.putString(Podcast.POSTER_KEY, posterUrl);
            allEpisodesArgs.putString(Podcaster.PUSH_ID_KEY, podcasterId);
            allEpisodesArgs.putString(Episode.EPISODES_KEY, episodesId);

            aboutPodcastArgs = new Bundle();
            aboutPodcastArgs.putString(Podcast.DESCRIPTION_KEY, podcast.getDescription());
        }else {
            throw new IllegalArgumentException(PodcastPagerAdapter.class.getSimpleName()+": Bundle is null or does not contain Podcast.PODCAST_KEY");
        }
    }
}
