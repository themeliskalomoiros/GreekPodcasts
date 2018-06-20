package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class MainViewMvcImpl implements MainViewMvc {

    private View rootView;
    private ViewPager viewPager;
    private FloatingActionButton createPodcastFAB;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private MainPagerAdapter mainPagerAdapter;
    private TabLayout tabLayout;

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent, @NonNull FragmentManager fragmentManager) {
        this.rootView = inflater.inflate(R.layout.activity_main, parent, false);
        initialize(fragmentManager);
    }

    private void initialize(FragmentManager fragmentManager) {
        initializeViewPager(fragmentManager);
        initializeViews();
    }

    private void initializeViewPager(FragmentManager fragmentManager) {
        String[] tabLabels = {this.rootView.getContext().getString(R.string.tab_label_shows),
                this.rootView.getContext().getString(R.string.tab_label_categories),
                this.rootView.getContext().getString(R.string.tab_label_favorites)};
        this.mainPagerAdapter = new MainPagerAdapter(fragmentManager, tabLabels);
        this.viewPager = this.rootView.findViewById(R.id.viewPager);
        this.viewPager.setAdapter(this.mainPagerAdapter);
    }

    private void initializeViews() {
        this.createPodcastFAB = this.rootView.findViewById(R.id.fab_create_podcast);
        this.toolbar = this.rootView.findViewById(R.id.toolbar);
        this.collapsingToolbarLayout = this.rootView.findViewById(R.id.collapsing_toolbar_layout);
        this.collapsingToolbarLayout.setTitle(this.collapsingToolbarLayout.getContext().getString(R.string.app_name));
        this.tabLayout = this.rootView.findViewById(R.id.tabLayout);
        this.tabLayout.setupWithViewPager(this.viewPager);
    }

    @Override
    public View getRootView() {
        return this.rootView;
    }

    @Override
    public Toolbar getToolbar() {
        return this.toolbar;
    }

    @Override
    public void setOnCreatePodcastClickListener(OnActionCreatePodcastClickListener listener) {
        this.createPodcastFAB.setOnClickListener(view -> {
            if (listener != null)
                listener.onActionCreatePodcastClick();
        });
    }
}
