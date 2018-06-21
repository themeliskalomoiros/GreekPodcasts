package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcast_screen;

import android.os.Bundle;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class PodcastScreenViewMvcImpl implements PodcastScreenViewMvc {

    private View rootView;
    private TextView title, podcasterName;
    private ImageView poster;
    private FloatingActionButton playFab;
    private Toolbar toolbar;
    private FrameLayout quickPlayerContainer;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    private boolean onLand;

    public PodcastScreenViewMvcImpl(LayoutInflater inflater, ViewGroup parent, @NonNull
            FragmentManager fragmentManager, Bundle viewPagerArgs) {
        this.rootView = inflater.inflate(R.layout.activity_podcast, parent, false);
        this.initialize(fragmentManager, viewPagerArgs);
    }

    private void initialize(FragmentManager manager, Bundle fragmentArgs) {
        initializeViews();
        setUpViewPagerAndTabLayout(manager, fragmentArgs);
    }

    @Override
    public int getMainContentContainerCurrentItem() {
        if (this.viewPager != null) {
            return this.viewPager.getCurrentItem();
        }
        return 0;
    }

    @Override
    public void bindPoster(String url) {
        Picasso.get().load(url).placeholder(R.drawable.ic_headset_black_light_148dp)
                .error(R.drawable.ic_error_black_light_148dp)
                .into(this.poster);
    }

    @Override
    public void bindPodcastTitle(String title) {
        if (this.onLand) {
            CollapsingToolbarLayout collapsingToolbarLayout = this.rootView.findViewById(R.id.collapsing_toolbar_layout);
            collapsingToolbarLayout.setTitle(title);
        } else {
            this.title.setText(title);
        }
    }

    @Override
    public void bindPodcasterName(String name) {
        String prefix = this.rootView.getContext().getString(R.string.podcaster_prefix);
        this.podcasterName.setText(prefix + " " + name);
    }

    @Override
    public Toolbar getToolbar() {
        return this.toolbar;
    }

    @Override
    public int getMainContentContainerId() {
        return R.id.viewPager;
    }

    @Override
    public int getMainContentCurrentItemPosition() {
        return this.viewPager.getCurrentItem();
    }

    @Override
    public int getQuickPlayerContainerId() {
        return R.id.quick_player_container;
    }

    @Override
    public void setOnActionPlayClickListener(OnActionPlayClickListener listener) {
        this.playFab.setOnClickListener(v -> {
            if (listener != null)
                listener.onActionPlayClick();
        });
    }

    @Override
    public View getRootView() {
        return this.rootView;
    }

    private void setUpViewPagerAndTabLayout(FragmentManager manager, Bundle fragmentArgs) {
        String[] tabTitles = {this.rootView.getContext().getString(R.string.tab_label_episodes),
                this.rootView.getContext().getString(R.string.tab_label_about)};
        PodcastPagerAdapter podcastPagerAdapter = new PodcastPagerAdapter(manager, fragmentArgs, tabTitles);
        this.viewPager.setAdapter(podcastPagerAdapter);
        this.tabLayout.setupWithViewPager(this.viewPager);
    }

    private void initializeViews() {
        this.title = this.rootView.findViewById(R.id.podcast_title_textview);
        if (this.title == null) {
            this.onLand = true;
        }
        this.podcasterName = this.rootView.findViewById(R.id.podcaster_textView);
        this.poster = this.rootView.findViewById(R.id.poster_imageview);
        this.playFab = this.rootView.findViewById(R.id.play_fab);
        this.toolbar = this.rootView.findViewById(R.id.toolbar);
        this.quickPlayerContainer = this.rootView.findViewById(R.id.quick_player_container);
        this.viewPager = this.rootView.findViewById(R.id.viewPager);
        this.tabLayout = this.rootView.findViewById(R.id.tabLayout);
    }
}
