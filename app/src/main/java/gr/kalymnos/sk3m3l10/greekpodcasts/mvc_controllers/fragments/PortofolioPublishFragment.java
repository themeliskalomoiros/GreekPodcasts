package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish.PortofolioPublishViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish.PortofolioPublishViewMvcImpl;

public class PortofolioPublishFragment extends Fragment {

    private PortofolioPublishViewMvc viewMvc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMvc = new PortofolioPublishViewMvcImpl(inflater, container);
        return viewMvc.getRootView();
    }
}
