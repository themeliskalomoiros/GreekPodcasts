package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public class FragmentUtils {

    private FragmentUtils() {
    }

    public static String fragmentTag(int viewpagerId, int currentItem) {
        //  Hack for getting tag of a fragment held by ViewPager
        //  According to https://learnpainless.com/android/how-to-get-fragment-from-viewpager-android
        //  This hack works only if ViewPagerAdapter extends FragmentPagerAdapter and not FragmentStatePagerAdapter
        return String.format("android:switcher:%d:%d", viewpagerId, currentItem);
    }
}
