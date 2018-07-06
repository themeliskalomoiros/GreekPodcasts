package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.PortofolioCreateFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.PortofolioPersonalFragment;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.PortofolioPublishFragment;

public class PortofolioPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = PortofolioPagerAdapter.class.getSimpleName();
    private Fragment[] fragmentArray;
    private String[] titlesArray;

    public PortofolioPagerAdapter(FragmentManager fm, @NonNull String... titles) {
        super(fm);
        titlesArray = titles;
    }

    public Fragment[] getFragmentsArray() {
        if (fragmentArray == null) {
            initializeFragmentArray();
        }
        return fragmentArray;
    }

    private void initializeFragmentArray() {
        fragmentArray = new Fragment[PortofolioViewMvc.TAB_COUNT];
        fragmentArray[0] = new PortofolioCreateFragment();
        fragmentArray[1] = new PortofolioPublishFragment();
        fragmentArray[2] = new PortofolioPersonalFragment();
    }

    @Override
    public int getCount() {
        return getFragmentsArray().length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (titlesArray != null && titlesArray.length == PortofolioViewMvc.TAB_COUNT) {
            return titlesArray[position];
        }else{
            throw new UnsupportedOperationException(TAG+": title array is null or length less than "+PortofolioViewMvc.TAB_COUNT);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return getFragmentsArray()[position];
    }
}
