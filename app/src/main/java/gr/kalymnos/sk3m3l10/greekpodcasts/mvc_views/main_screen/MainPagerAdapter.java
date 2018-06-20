package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllCategoriesFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllFavoritesFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.AllPodcastsFragment;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen.MainViewMvc.TAB_COUNT;

public class MainPagerAdapter extends FragmentPagerAdapter {

    protected Fragment[] fragmentsArray;
    protected String[] titlesArray;

    public MainPagerAdapter(FragmentManager fm, @NonNull String... titles) {
        super(fm);
        this.titlesArray = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragmentsArray[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.titlesArray[position];
    }

    @Override
    public int getCount() {
        return this.getFragmentsArray().length;
    }

    public Fragment[] getFragmentsArray() {
        if (fragmentsArray == null) {
            fragmentsArray = new Fragment[TAB_COUNT];
            fragmentsArray[0] = new AllPodcastsFragment();
            fragmentsArray[1] = new AllCategoriesFragment();
            fragmentsArray[2] = new AllFavoritesFragment();
        }
        return fragmentsArray;
    }
}
