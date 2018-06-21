package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.about_podcast.AboutPodcastViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.about_podcast.AboutPodcastViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class AboutPodcastFragment extends Fragment {

    private AboutPodcastViewMvc viewMvc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.viewMvc = new AboutPodcastViewMvcImpl(inflater, container);
        Bundle args = this.getArguments();
        if (args != null && args.containsKey(Podcast.DESCRIPTION_KEY)) {
            this.viewMvc.bindText(args.getString(Podcast.DESCRIPTION_KEY));
        }
        return this.viewMvc.getRootView();
    }
}
