package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.create;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class PortofolioCreateViewMvcImpl implements PortofolioCreateViewMvc {

    private View rootView;
    private EditText titleEditText, descriptionEditText;
    private Spinner categorySpinner;
    private ImageView updatePodcastImageView;
    private ProgressBar progressBar;

    public PortofolioCreateViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.portofolio_creation, parent, false);
        titleEditText = rootView.findViewById(R.id.podcast_title_editText);
        descriptionEditText = rootView.findViewById(R.id.description_editText);
        categorySpinner = rootView.findViewById(R.id.categories_spinner);
        updatePodcastImageView = rootView.findViewById(R.id.update_podcast_pic_imageview);
        progressBar = rootView.findViewById(R.id.pb_loading_indicator);
    }

    @Override
    public void bindPoster(Bitmap poster) {
        updatePodcastImageView.setImageBitmap(poster);
    }

    @Override
    public int getPosterContainerHeight() {
        return updatePodcastImageView.getHeight();
    }

    @Override
    public int getPosterContainerWidth() {
        return updatePodcastImageView.getWidth();
    }

    @Override
    public void enableSpinner(boolean enable) {
        categorySpinner.setEnabled(enable);
    }

    @Override
    public void displayLoadingIndicator(boolean display) {
        if (display){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setOnPosterClickListener(OnPosterClickListener listener) {
        updatePodcastImageView.setOnClickListener(view -> {
            if (listener != null)
                listener.onPosterClick();
        });
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
