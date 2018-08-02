package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class MainViewMvcImpl implements MainViewMvc {

    private View rootView;
    private ViewPager viewPager;
    private FloatingActionButton createPodcastFAB;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private MainPagerAdapter mainPagerAdapter;
    private TabLayout tabLayout;
    private AdView adView;
    private AppBarLayout appBarLayout;

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent, @NonNull FragmentManager fragmentManager) {
        rootView = inflater.inflate(R.layout.activity_main, parent, false);
        initialize(fragmentManager);
    }

    private void initialize(FragmentManager fragmentManager) {
        initializeViewPager(fragmentManager);
        initializeViews();
    }

    private void initializeViewPager(FragmentManager fragmentManager) {
        String[] tabLabels = {rootView.getContext().getString(R.string.tab_label_shows),
                rootView.getContext().getString(R.string.tab_label_categories),
                rootView.getContext().getString(R.string.tab_label_favorites)};
        mainPagerAdapter = new MainPagerAdapter(fragmentManager, tabLabels);
        viewPager = rootView.findViewById(R.id.viewPager);
        viewPager.setAdapter(mainPagerAdapter);
    }

    private void initializeViews() {
        createPodcastFAB = rootView.findViewById(R.id.fab_create_podcast);
        toolbar = rootView.findViewById(R.id.toolbar);
        collapsingToolbarLayout = rootView.findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(collapsingToolbarLayout.getContext().getString(R.string.app_name));
        tabLayout = rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        adView = rootView.findViewById(R.id.adview);
        appBarLayout = rootView.findViewById(R.id.appbar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    //  Careful, there should be a space between double quote otherwise it wont work
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(rootView.getContext().getString(R.string.app_name));
                    isShow=false;
                }
            }
        });
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void setOnCreatePodcastClickListener(OnActionCreatePodcastClickListener listener) {
        createPodcastFAB.setOnClickListener(view -> {
            if (listener != null)
                listener.onActionCreatePodcastClick();
        });
    }

    @Override
    public void loadAd(AdRequest adRequest) {
        adView.loadAd(adRequest);
    }
}
