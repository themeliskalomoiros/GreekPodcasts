package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class PortofolioViewMvcImpl implements PortofolioViewMvc {

    private View rootView;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private FloatingActionButton saveButton;

    public PortofolioViewMvcImpl(LayoutInflater inflater, ViewGroup parent, @NonNull FragmentManager fragmentManager) {
        this.rootView = inflater.inflate(R.layout.activity_portofolio, parent, false);
        initialize(fragmentManager);
    }

    private void initialize(FragmentManager fragmentManager) {
        initializeViewPager(fragmentManager);
        initializeViews();
    }

    private void initializeViews() {
        viewPager = rootView.findViewById(R.id.viewPager);
        toolbar = rootView.findViewById(R.id.toolbar);
        saveButton = rootView.findViewById(R.id.fab_save);
    }

    private void initializeViewPager(FragmentManager fragmentManager) {

    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void setOnActionSaveClickListener(OnActionSaveClickListener listener) {

    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
