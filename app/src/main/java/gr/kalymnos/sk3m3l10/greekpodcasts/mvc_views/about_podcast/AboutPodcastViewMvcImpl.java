package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.about_podcast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class AboutPodcastViewMvcImpl implements AboutPodcastViewMvc {

    private View rootView;
    private TextView aboutTextView;

    public AboutPodcastViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.about_podcast, parent, false);
        aboutTextView = rootView.findViewById(R.id.about_textview);
    }

    @Override
    public void bindText(String text) {
        aboutTextView.setText(text);
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
