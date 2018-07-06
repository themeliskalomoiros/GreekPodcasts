package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.create;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class PortofolioCreateViewMvcImpl implements PortofolioCreateViewMvc {

    private static final String TAG = PortofolioCreateViewMvcImpl.class.getSimpleName();
    private View rootView;
    private EditText titleEditText, descriptionEditText;
    private Spinner categorySpinner;
    private ArrayAdapter<String> spinnerAdapter;
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
    public void displayLoadingIndicator(boolean display) {
        if (display) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void addCategoriesToSpinner(String[] titles) {
        if (spinnerAdapter == null) {
            spinnerAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, titles);
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);
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
    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        if (listener != null) {
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    listener.onCategoryChosen(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    listener.onNothingChosen();
                }
            });
        }
    }

    @Override
    public String getTitleText() {
        return titleEditText.getText().toString();
    }

    @Override
    public String getDescriptionText() {
        return descriptionEditText.getText().toString();
    }

    @Override
    public ImageView getPosterImageView() {
        return updatePodcastImageView;
    }

    @Override
    public int getCategoryPosition() {
        return categorySpinner.getSelectedItemPosition();
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
