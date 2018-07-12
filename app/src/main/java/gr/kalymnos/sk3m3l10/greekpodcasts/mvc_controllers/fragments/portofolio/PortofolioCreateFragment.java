package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.create.PortofolioCreateViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.create.PortofolioCreateViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.BitmapUtils;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.FileUtils;

public class PortofolioCreateFragment extends Fragment implements PortofolioCreateViewMvc.OnPosterClickListener,
        PortofolioCreateViewMvc.OnCategorySelectedListener, LoaderManager.LoaderCallbacks<List<Category>>,
        ChangeSaver, DataRepository.OnCreatedPodcastListener {

    private static final String TAG = PortofolioCreateFragment.class.getSimpleName();
    private static final int LOADER_ID = 121;

    private PortofolioCreateViewMvc viewMvc;
    private static final int RC_POSTER_PIC = 1331;

    //  Cache uri instead of Bitmap because the latter is too large (could be more than 5Mb) and
    //  throws an exception!
    private Uri cachedPosterUri;

    private List<Category> cachedCategories;

    private DataRepository repo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMvc = new PortofolioCreateViewMvcImpl(inflater, container);
        viewMvc.setOnPosterClickListener(this);
        //  TODO: Replace with a real service
        repo = new StaticFakeDataRepo();
        repo.setOnCreatedPodcastListener(this);
        return viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        boolean isValidBundle = savedInstanceState != null && savedInstanceState.containsKey(Podcast.POSTER_KEY)
                && savedInstanceState.containsKey(Category.CATEGORIES_KEY);

        if (isValidBundle) {
            cachedPosterUri = savedInstanceState.getParcelable(Podcast.POSTER_KEY);
            cachedCategories = savedInstanceState.getParcelableArrayList(Category.CATEGORIES_KEY);
        }

        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cachedPosterUri != null) {
            String fileName = FileUtils.fileName(getContext().getContentResolver(), cachedPosterUri);
            viewMvc.bindImageFileName(fileName);
            viewMvc.displayImageHint(false);
            viewMvc.displayImageFileName(true);
            viewMvc.bindPoster(cachedPosterUri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_POSTER_PIC) {
            boolean imageFileFetched = resultCode == getActivity().RESULT_OK && data != null;
            if (imageFileFetched) {
                cachedPosterUri = data.getData();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (cachedPosterUri != null) {
            outState.putParcelable(Podcast.POSTER_KEY, cachedPosterUri);
        }
        outState.putParcelableArrayList(Category.CATEGORIES_KEY, (ArrayList<? extends Parcelable>) cachedCategories);
    }

    @Override
    public void onPosterClick() {
        //  Open gallery so user can pick an image.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, RC_POSTER_PIC);
        }
    }

    @NonNull
    @Override
    public Loader<List<Category>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Category>>(getContext()) {

            @Override
            protected void onStartLoading() {
                if (cachedCategories != null) {
                    deliverResult(cachedCategories);
                } else {
                    viewMvc.displayLoadingIndicator(true);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Category> loadInBackground() {
                return repo.fetchAllCategories();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Category>> loader, List<Category> data) {
        if (data != null) {
            cachedCategories = data;
            viewMvc.displayLoadingIndicator(false);

            String[] categoryTitles = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                categoryTitles[i] = data.get(i).getTitle();
            }
            viewMvc.addCategoriesToSpinner(categoryTitles);
            viewMvc.setOnCategorySelectedListener(this);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Category>> loader) {

    }

    @Override
    public void onCategoryChosen(int position) {
        //  TODO: Do something when a category from the spinner is chosen (optional)
    }

    @Override
    public void onNothingChosen() {
        //  TODO: Do something when no item from the spinner is chosen (optional)
    }

    @Override
    public void save() {
        boolean isTitleValid = !TextUtils.isEmpty(viewMvc.getTitleText())
                && viewMvc.getTitleText().length() > MIN_TITLE_LENGTH
                && viewMvc.getTitleText().length() <= MAX_TITLE_LENGTH;

        boolean isDescriptionValid = !TextUtils.isEmpty(viewMvc.getDescriptionText())
                && viewMvc.getDescriptionText().length() > MIN_DESCRIPTION_LENGTH
                && viewMvc.getDescriptionText().length() <= MAX_DESCRIPTION_LENGTH;

        boolean imageDataExists = viewMvc.getPosterImageView().getDrawable() != null;

        if (isTitleValid && isDescriptionValid && imageDataExists) {

            //  Initialize a podcast with as many info we have so far...
            Podcast podcastToBeCreated = new Podcast();
            podcastToBeCreated.setTitle(viewMvc.getTitleText());
            podcastToBeCreated.setDescription(viewMvc.getDescriptionText());
            podcastToBeCreated.setCategoryId(cachedCategories.get(viewMvc.getCategoryPosition()).getFirebasePushId());

            repo.createNewPodcast(podcastToBeCreated);
        } else {
            //  TODO: pop-up a message explaining why the save cannot be done.
            Toast.makeText(getContext(), "Could not save", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void onPodcastCreationSuccess(String podcastPushId) {
        byte[] posterData = BitmapUtils.getBytesFromImageView(viewMvc.getPosterImageView());
        repo.uploadImage(podcastPushId, posterData);
    }

    @Override
    public void onPodcastCreationFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
