package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish;

import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.BitmapUtils;

public class PortofolioPublishViewMvcImpl implements PortofolioPublishViewMvc {

    private static final String TAG = PortofolioPublishViewMvcImpl.class.getSimpleName();

    private View rootView;
    private Spinner podcastSpinner, categorySpinner;
    private RecyclerView episodesRecyclerView;
    private ImageView posterImageView;
    private TextView descriptionTextView, imageHintTextView, imageFileNameTextView;
    private ImageButton addEpisodeButton, editTitleButton, editDescriptionButton;
    private Button viewAllEpisodesButton;
    private ArrayAdapter<String> podcastSpinnerAdapter, categorySpinnerAdapter;
    private ProgressBar podcastBar, categoryBar, episodesBar;
    private EpisodesAdapter episodesAdapter;
    private FrameLayout allEpisodesFragmentContainer;

    public PortofolioPublishViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    @Override
    public void bindEpisodes(List<Episode> episodes) {
        if (episodesAdapter != null) {
            if (episodes != null && episodes.size() > 0) {
                episodesAdapter.addEpisodes(episodes);
                episodesAdapter.notifyDataSetChanged();
            }else{
                List<Episode> emptyList = new ArrayList<>();
                episodesAdapter.addEpisodes(emptyList);
                episodesAdapter.notifyDataSetChanged();
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
    public void bindPoster(Uri uri) {
        Picasso.get().load(uri)
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

            posterImageView.setOnClickListener(view -> listener.onPosterClick());
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
    public void setOnItemsSelectedListener(OnItemsSelectedListener listener) {
        if (listener != null) {
            podcastSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    listener.onPodcastSelected(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    listener.onCategorySelected(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    @Override
    public void setCategorySelection(int position) {
        categorySpinner.setSelection(position);
    }

    @Override
    public int getAllEpisodesContainerId() {
        if (allEpisodesFragmentContainer != null) {
            return allEpisodesFragmentContainer.getId();
        }
        return 0;
    }

    @Override
    public int getTitleDialogTitleRes() {
        return R.string.insert_new_title_label;
    }

    @Override
    public int getDescriptionDialogTitleRes() {
        return R.string.insert_description_label;
    }

    @Override
    public void displayImageHint(boolean display) {
        if (display) {
            imageHintTextView.setVisibility(View.VISIBLE);
        } else {
            imageHintTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void displayImageFileName(boolean display) {
        if (display) {
            imageFileNameTextView.setVisibility(View.VISIBLE);
        } else {
            imageFileNameTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getPosterContainerWidth() {
        return posterImageView.getWidth();
    }

    @Override
    public int getPosterContainerHeight() {
        return posterImageView.getHeight();
    }

    @Override
    public byte[] getPosterData() {
        return BitmapUtils.getBytesFromImageView(posterImageView);
    }

    @Override
    public String getPodcastTitle() {
        return podcastSpinner.getSelectedItem().toString();
    }

    @Override
    public String getDescription() {
        return descriptionTextView.getText().toString();
    }

    @Override
    public boolean posterExists() {
        return posterImageView.getDrawable() != null;
    }

    @Override
    public void bindImageFileName(String name) {
        imageFileNameTextView.setText(name);
    }

    @Override
    public void selectPodcastSpinnerItem(int position) {
        podcastSpinner.setSelection(position);
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
        podcastSpinnerAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, titles);
        podcastSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        podcastSpinner.setAdapter(podcastSpinnerAdapter);
        podcastSpinnerAdapter.notifyDataSetChanged();
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
    public int getSelectedCategoryPosition() {
        if (categorySpinner != null && categorySpinnerAdapter != null) {
            return categorySpinner.getSelectedItemPosition();
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
        allEpisodesFragmentContainer = rootView.findViewById(R.id.all_episodes_fragment_container);
        imageFileNameTextView = rootView.findViewById(R.id.chosen_image_file_name);
        imageHintTextView = rootView.findViewById(R.id.click_the_icon_label);
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
