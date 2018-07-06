package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal.PortofolioPersonalViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal.PortofolioPersonalViewMvcImpl;

public class PortofolioPersonalFragment extends Fragment {

    private PortofolioPersonalViewMvc viewMvc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMvc = new PortofolioPersonalViewMvcImpl(inflater, container);
        return viewMvc.getRootView();
    }
}
