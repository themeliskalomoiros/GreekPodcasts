package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;

public class PortofolioPublishViewMvcImpl implements PortofolioPublishViewMvc {

    private View rootView;
    private Spinner podcastSpinner, categorySpinner;
    private RecyclerView episodesRecyclerView;
    private ImageView posterImageView;
    private TextView descriptionTextView;
    private ImageButton addEpisodeButton, editTitleButton, editDescriptionButton;

    public PortofolioPublishViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    @Override
    public void bindEpisodes(List<Episode> episodes) {

    }

    @Override
    public void bindPoster(Bitmap poster) {
        posterImageView.setImageBitmap(poster);
    }

    @Override
    public void bindDescription(String description) {
        descriptionTextView.setText(description);
    }

    @Override
    public void setOnButtonsClickListener(OnButtonsClickListener listener) {

    }

    @Override
    public void setOnSpinnerChosenListener(OnSpinnerChosenListener listener) {

    }

    @Override
    public boolean onLand() {
        if (episodesRecyclerView != null) {
            return true;
        }
        return false;
    }

    @Override
    public void bindTitle(String title) {

    }

    @Override
    public View getRootView() {
        return rootView;
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.portofolio_published, parent, false);
        podcastSpinner = rootView.findViewById(R.id.choose_podcast_spinner);
        podcastSpinner = rootView.findViewById(R.id.categories_spinner);
        episodesRecyclerView = rootView.findViewById(R.id.recycler_view);
        posterImageView = rootView.findViewById(R.id.podcast_pic_imageview);
        descriptionTextView = rootView.findViewById(R.id.description_textview);
        addEpisodeButton = rootView.findViewById(R.id.add_episode_imagebutton);
        editTitleButton = rootView.findViewById(R.id.edit_podcast_title_imagebutton);
        editDescriptionButton = rootView.findViewById(R.id.edit_description_imagebutton);
    }
}
