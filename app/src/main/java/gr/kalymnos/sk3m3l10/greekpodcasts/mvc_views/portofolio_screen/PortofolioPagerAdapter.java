package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.PortofolioCreateFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.PortofolioPersonalFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.PortofolioPublishFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen.MainPagerAdapter;

public class PortofolioPagerAdapter extends MainPagerAdapter {

    public PortofolioPagerAdapter(FragmentManager fm, @NonNull String... titles) {
        super(fm, titles);
    }

    @Override
    public Fragment[] getFragmentsArray() {
        if (fragmentsArray == null) {
            Fragment[] fragments = new Fragment[PortofolioViewMvc.TAB_COUNT];
            fragments[0] = new PortofolioCreateFragment();
            fragments[1] = new PortofolioPublishFragment();
            fragments[2] = new PortofolioPersonalFragment();
        }
        return fragmentsArray;
    }
}
