package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;

public class PortofolioPublishViewMvcImpl implements PortofolioPublishViewMvc {

    private static final String TAG = PortofolioPublishViewMvcImpl.class.getSimpleName();

    private View rootView;
    private Spinner podcastSpinner, categorySpinner;
    private RecyclerView episodesRecyclerView;
    private ImageView posterImageView;
    private TextView descriptionTextView;
    private ImageButton addEpisodeButton, editTitleButton, editDescriptionButton;
    private Button viewAllEpisodesButton;
    private ArrayAdapter<String> podcastSpinnerAdapter, categorySpinnerAdapter;
    private ProgressBar podcastBar, categoryBar, episodesBar;
    private EpisodesAdapter episodesAdapter;

    public PortofolioPublishViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    @Override
    public void bindEpisodes(List<Episode> episodes) {
        if (episodesAdapter != null) {
            if (episodes != null && episodes.size() > 0){
                episodesAdapter.addEpisodes(episodes);
            }
        }
    }

    @Override
    public void bindPoster(String url) {
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_headset_black_light_148dp)
                .error(R.drawable.ic_error_black_light_148dp)
                .into(posterImageView);
    }

    @Override
    public void bindDescription(String description) {
        descriptionTextView.setText(description);
    }

    @Override
    public void setOnButtonsClickListener(OnButtonsClickListener listener) {
        if (listener != null) {
            //  Some views may exist only in land, so they could be null
            if (addEpisodeButton != null) {
                addEpisodeButton.setOnClickListener(view -> listener.onAddEpisodeClick());
            }

            if (viewAllEpisodesButton != null) {
                viewAllEpisodesButton.setOnClickListener(view -> listener.onViewEpisodesClick());
            }

            editTitleButton.setOnClickListener(view -> listener.onEditPodcastClick(podcastSpinner.getSelectedItemPosition()));

            editDescriptionButton.setOnClickListener(view -> listener.onEditDescriptionClick());
        }
    }

    @Override
    public void setOnPodcastSelectedListener(OnPodcastSelectedListener listener) {
        if (listener != null) {
            podcastSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    listener.onPodcastSelected(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    @Override
    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        if (listener != null) {
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    listener.onCategorySelected(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    @Override
    public boolean onLand() {
        if (episodesRecyclerView != null) {
            return true;
        }
        return false;
    }

    @Override
    public void addPodcastsToSpinner(String[] titles) {
        if (podcastSpinnerAdapter == null) {
            podcastSpinnerAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, titles);
            podcastSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            podcastSpinner.setAdapter(podcastSpinnerAdapter);
        }
    }

    @Override
    public void addCategoriesToSpinner(String[] titles) {
        if (categorySpinnerAdapter == null) {
            categorySpinnerAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, titles);
            categorySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            categorySpinner.setAdapter(categorySpinnerAdapter);
        }
    }

    @Override
    public void bindTitle(String title) {

    }

    @Override
    public void displayPodcastLoadingIndicator(boolean display) {
        if (display) {
            podcastBar.setVisibility(View.VISIBLE);
        } else {
            podcastBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void displayCategoryLoadingIndicator(boolean display) {
        if (display) {
            categoryBar.setVisibility(View.VISIBLE);
        } else {
            categoryBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void displayEpisodesLoadingIndicator(boolean display) {
        if (episodesBar != null) {
            if (display) {
                episodesBar.setVisibility(View.VISIBLE);
            } else {
                episodesBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getSelectedPodcastPosition() {
        if (podcastSpinner != null && podcastSpinnerAdapter != null) {
            return podcastSpinner.getSelectedItemPosition();
        } else {
            throw new UnsupportedOperationException(TAG + ": podcastSpinner or podcastSpinnerAdapter is null.");
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.portofolio_published, parent, false);
        podcastSpinner = rootView.findViewById(R.id.choose_podcast_spinner);
        categorySpinner = rootView.findViewById(R.id.categories_spinner);
        posterImageView = rootView.findViewById(R.id.podcast_pic_imageview);
        descriptionTextView = rootView.findViewById(R.id.description_textview);
        addEpisodeButton = rootView.findViewById(R.id.add_episode_imagebutton);
        editTitleButton = rootView.findViewById(R.id.edit_podcast_title_imagebutton);
        editDescriptionButton = rootView.findViewById(R.id.edit_description_imagebutton);
        viewAllEpisodesButton = rootView.findViewById(R.id.view_all_episodes_button);
        episodesBar = rootView.findViewById(R.id.episodes_loading_indicator);
        categoryBar = rootView.findViewById(R.id.category_loading_indicator);
        podcastBar = rootView.findViewById(R.id.podcast_loading_indicator);
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        episodesRecyclerView = rootView.findViewById(R.id.recycler_view);
        if (episodesRecyclerView != null) {
            //  RecyclerView does not exists on portrait layout
            episodesAdapter = new EpisodesAdapter(rootView.getContext());
            LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
            episodesRecyclerView.setLayoutManager(layoutManager);
            episodesRecyclerView.setHasFixedSize(true);
            episodesRecyclerView.setAdapter(episodesAdapter);
        }
    }
}
