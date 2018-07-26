package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities.AddEpisodeActivity;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish.PortofolioPublishViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish.PortofolioPublishViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.FileUtils;

public class PortofolioPublishFragment extends Fragment implements PortofolioPublishViewMvc.OnItemsSelectedListener,
        ChangeSaver, PortofolioPublishViewMvc.OnButtonsClickListener {

    private static final String TAG = PortofolioPublishFragment.class.getSimpleName();

    private static final int RC_POSTER_PIC = 1323;
    private Uri cachedPosterUri;

    private List<Podcast> cachedPodcasts;
    private List<Episode> cachedEpisodes;
    private List<Category> cachedCategories;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference categoriesRef, podcastsRef;

    private PortofolioPublishViewMvc viewMvc;

    private InsertTextDialogFragment titleDialog, descriptionDialog;
    private InsertTextDialogFragment.OnInsertedTextListener titleInsertedListener = text -> {
        if (cachedPodcasts != null) {

            String[] originalTitles = createPodcastTitles();
            //  Swap the new title
            originalTitles[viewMvc.getSelectedPodcastPosition()] = text;

            viewMvc.addPodcastsToSpinner(originalTitles);
        }
    },
            descriptionInsertedListener = text -> viewMvc.bindDescription(text);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initialize(inflater, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeFirebase();

        if (isValidInstanceState(savedInstanceState)) {
            restoreCachedData(savedInstanceState);
        } else {
            fetchData();
        }
    }

    private void fetchData() {
        viewMvc.displayCategoryLoadingIndicator(true);
        //  Fetch categories first, then podcasts
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bindCategoriesTitles(dataSnapshot, loadUserPodcasts());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private Runnable loadUserPodcasts() {
                return () -> {
                    viewMvc.displayPodcastLoadingIndicator(true);
                    podcastsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            fetchPodcastsAndBindSpinner(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                        private void fetchPodcastsAndBindSpinner(DataSnapshot dataSnapshot) {
                            viewMvc.displayPodcastLoadingIndicator(false);

                            List<Podcast> podcastList = new ArrayList<>();

                            for (DataSnapshot podcastSnapshot : dataSnapshot.getChildren()) {
                                Podcast podcast = podcastSnapshot.getValue(Podcast.class);
                                podcast.setFirebasePushId(podcastSnapshot.getKey());
                                if (podcast != null) {
                                    boolean podcastBelongsToCurrentUser = podcast.getPodcasterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    if (podcastBelongsToCurrentUser) {
                                        podcastList.add(podcast);
                                    }
                                }
                            }

                            if (isListValid(podcastList)) {
                                cachedPodcasts = podcastList;
                                viewMvc.addPodcastsToSpinner(createPodcastTitles());
                            }
                        }

                        @NonNull
                        private String[] createPodcastTitles() {
                            String[] titles = new String[cachedPodcasts.size()];
                            for (int i = 0; i < cachedPodcasts.size(); i++) {
                                titles[i] = cachedPodcasts.get(i).getTitle();
                            }
                            return titles;
                        }
                    });
                };
            }

            private void bindCategoriesTitles(DataSnapshot dataSnapshot, Runnable actionAfterCompletion) {
                viewMvc.displayCategoryLoadingIndicator(false);

                List<Category> categoryList = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }

                if (isListValid(categoryList)) {
                    cachedCategories = categoryList;

                    //  Create the titles array.
                    String[] titles = createCategoriesTitles();
                    viewMvc.addCategoriesToSpinner(titles);

                    getActivity().runOnUiThread(actionAfterCompletion);
                }


            }
        });
    }

    @NonNull
    private String[] createCategoriesTitles() {
        String[] titles = new String[cachedCategories.size()];
        for (int i = 0; i < cachedCategories.size(); i++) {
            titles[i] = cachedCategories.get(i).getTitle();
        }
        return titles;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cachedPosterUri != null) {
            viewMvc.bindPoster(cachedPosterUri);
            String fileName = FileUtils.fileName(getContext().getContentResolver(), cachedPosterUri);
            viewMvc.displayImageHint(false);
            viewMvc.displayImageFileName(true);
            viewMvc.bindImageFileName(fileName);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_POSTER_PIC) {
            boolean pictureDataFetched = resultCode == getActivity().RESULT_OK && data != null;

            if (pictureDataFetched) {
                cachedPosterUri = data.getData();
            }
        }
    }

    @Override
    public void onPodcastSelected(int position) {
        if (isListValid(cachedPodcasts)) {
            Podcast podcastSelected = cachedPodcasts.get(position);

            if (cachedPosterUri == null) {
                viewMvc.bindPoster(podcastSelected.getPosterUrl());
            }

            viewMvc.bindDescription(podcastSelected.getDescription());

            if (isListValid(cachedCategories)) {
                for (int i = 0; i < cachedCategories.size(); i++) {
                    if (podcastSelected.getCategoryId().equals(cachedCategories.get(i).getFirebasePushId())) {
                        //  Found a cached category which matches the podcasts category id, choose it on spinner
                        viewMvc.setCategorySelection(i);
                        break;
                    }
                }
            }

            viewMvc.displayEpisodesLoadingIndicator(true);
            getEpisodesReference(podcastSelected.getFirebasePushId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    viewMvc.displayEpisodesLoadingIndicator(false);

                    List<Episode> episodeList = new ArrayList<>();
                    for (DataSnapshot episodeSnapshot : dataSnapshot.getChildren()) {
                        Episode episode = episodeSnapshot.getValue(Episode.class);
                        if (episode != null) {
                            episodeList.add(episode);
                        }
                    }

                    viewMvc.bindEpisodes(episodeList);

                    if (isListValid(episodeList)) {
                        cachedEpisodes = episodeList;
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private DatabaseReference getEpisodesReference(String podcastSelectedPushId) {
        return firebaseDatabase.getReference()
                        .child(ChildNames.EPISODES)
                        .child(podcastSelectedPushId);
    }

    @Override
    public void onCategorySelected(int position) {

    }

    private boolean isListValid(List<? extends Parcelable> list) {
        return list != null && list.size() > 0;
    }

    @Override
    public void save() {
        if (isValidStateToSave()) {
            Podcast selectedPodcast = cachedPodcasts.get(viewMvc.getSelectedPodcastPosition());
            boolean validTitle = !TextUtils.isEmpty(selectedPodcast.getTitle());
            boolean validPoster = viewMvc.posterExists();
            boolean validDescription = !TextUtils.isEmpty(getString(viewMvc.getDescriptionDialogTitleRes()));
            boolean userFilledInEverything = validTitle && validPoster && validDescription;

            if (userFilledInEverything) {
                //  TODO:   Update podcast with the new values
            }
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public boolean isValidStateToSave() {
        return cachedPosterUri != null && isListValid(cachedPodcasts) && isListValid(cachedCategories);
    }

    @Override
    public void onEditPodcastClick(int itemPosition) {
        if (titleDialog == null) {
            titleDialog = createDialog(viewMvc.getTitleDialogTitleRes());
            titleDialog.setOnInsertedTextListener(titleInsertedListener);
        }
        titleDialog.show(getFragmentManager(), getDialogFragmentTag(viewMvc.getTitleDialogTitleRes()));
    }

    @Override
    public void onEditDescriptionClick() {
        if (descriptionDialog == null) {
            descriptionDialog = createDialog(viewMvc.getDescriptionDialogTitleRes());
            descriptionDialog.setOnInsertedTextListener(descriptionInsertedListener);
        }
        descriptionDialog.show(getFragmentManager(), getDialogFragmentTag(viewMvc.getDescriptionDialogTitleRes()));
    }

    @Override
    public void onViewEpisodesClick() {
        boolean fragmentExists = getFragmentManager().findFragmentById(viewMvc.getAllEpisodesContainerId()) != null;
        if (!fragmentExists) {
            showViewAllEpisodesFragment();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (isListValid(cachedPodcasts))
            outState.putParcelableArrayList(Podcast.PODCASTS_KEY, (ArrayList<? extends Parcelable>) cachedPodcasts);

        if (isListValid(cachedEpisodes))
            outState.putParcelableArrayList(Episode.EPISODES_KEY, (ArrayList<? extends Parcelable>) cachedEpisodes);

        if (isListValid(cachedCategories))
            outState.putParcelableArrayList(Category.CATEGORIES_KEY, (ArrayList<? extends Parcelable>) cachedCategories);

        if (cachedPosterUri != null)
            outState.putParcelable(Podcast.POSTER_KEY, cachedPosterUri);
    }

    private void showViewAllEpisodesFragment() {
        if (!viewMvc.onLand()) {
            Bundle args = new Bundle();
            args.putParcelableArrayList(Episode.EPISODES_KEY, (ArrayList<? extends Parcelable>) cachedEpisodes);
            args.putParcelable(Podcast.PODCAST_KEY, cachedPodcasts.get(viewMvc.getSelectedPodcastPosition()));
            ViewAllEpisodesFragment episodesFragment = new ViewAllEpisodesFragment();
            episodesFragment.setArguments(args);
            getFragmentManager().beginTransaction().addToBackStack(null).replace(viewMvc.getAllEpisodesContainerId(), episodesFragment).commit();
        }
    }

    @Override
    public void onAddEpisodeClick() {
        Bundle extras = new Bundle();
        extras.putParcelable(Podcast.PODCAST_KEY, cachedPodcasts.get(viewMvc.getSelectedPodcastPosition()));
        Intent intent = new Intent(getContext(), AddEpisodeActivity.class);
        intent.putExtras(extras);
        getContext().startActivity(intent);
    }

    @Override
    public void onPosterClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, RC_POSTER_PIC);
        }
    }

    private void restoreCachedData(@NonNull Bundle savedInstanceState) {
        cachedPodcasts = savedInstanceState.getParcelableArrayList(Podcast.PODCASTS_KEY);
        cachedEpisodes = savedInstanceState.getParcelableArrayList(Episode.EPISODES_KEY);
        cachedCategories = savedInstanceState.getParcelableArrayList(Category.CATEGORIES_KEY);
        cachedPosterUri = savedInstanceState.getParcelable(Podcast.POSTER_KEY);

        viewMvc.addPodcastsToSpinner(createPodcastTitles());
        viewMvc.addCategoriesToSpinner(createCategoriesTitles());
    }

    private void initializeFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        categoriesRef = firebaseDatabase.getReference().child(ChildNames.CATEGORIES);
        podcastsRef = firebaseDatabase.getReference().child(ChildNames.PODCASTS);
    }

    private boolean isValidInstanceState(@Nullable Bundle savedInstanceState) {
        return savedInstanceState != null && savedInstanceState.containsKey(Podcast.PODCASTS_KEY)
                && savedInstanceState.containsKey(Episode.EPISODES_KEY)
                && savedInstanceState.containsKey(Category.CATEGORIES_KEY);
    }

    private InsertTextDialogFragment createDialog(int titleRes) {
        Bundle args = new Bundle();
        args.putInt(InsertTextDialogFragment.TITLE_KEY, titleRes);

        InsertTextDialogFragment dialogFragment = new InsertTextDialogFragment();
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    private String getDialogFragmentTag(int titleRes) {
        return InsertTextDialogFragment.TAG + String.valueOf(titleRes);
    }

    private View initialize(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        viewMvc = new PortofolioPublishViewMvcImpl(inflater, container);
        viewMvc.setOnItemsSelectedListener(this);
        viewMvc.setOnButtonsClickListener(this);
        return viewMvc.getRootView();
    }

    @NonNull
    private String[] createPodcastTitles() {
        String[] titles = new String[cachedPodcasts.size()];
        for (int i = 0; i < cachedPodcasts.size(); i++) {
            titles[i] = cachedPodcasts.get(i).getTitle();
        }
        return titles;
    }
}
