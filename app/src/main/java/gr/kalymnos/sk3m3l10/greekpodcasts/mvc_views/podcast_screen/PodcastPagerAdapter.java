package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen.PodcastScreenViewMvc.TAB_COUNT;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AboutPodcastFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllEpisodesFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen.MainPagerAdapter;

public class PodcastPagerAdapter extends MainPagerAdapter {


    public PodcastPagerAdapter(FragmentManager fm, @NonNull Bundle framgnetArgs,@NonNull String... titles) {
        super(fm, titles);
        this.fragmentArgs = framgnetArgs;
    }

    private Bundle fragmentArgs;

    public Fragment[] getFragmentsArray() {
        if (fragmentsArray == null) {
            fragmentsArray = new Fragment[TAB_COUNT];

            fragmentsArray[0] = new AllEpisodesFragment();
            fragmentsArray[0].setArguments(this.fragmentArgs);

            fragmentsArray[1] = new AboutPodcastFragment();
            fragmentsArray[1].setArguments(this.fragmentArgs);
        }
        return fragmentsArray;
    }
}
