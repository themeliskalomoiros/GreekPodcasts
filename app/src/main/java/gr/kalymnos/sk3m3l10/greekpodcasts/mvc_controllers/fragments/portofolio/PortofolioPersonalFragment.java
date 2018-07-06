package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal.PortofolioPersonalViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal.PortofolioPersonalViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public class PortofolioPersonalFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object>,
        ChangeSaver {

    private static final int PROMOTION_LOADER_ID = 100;
    private static final int PODCASTER_LOADER_ID = 200;
    private static final String TAG = PortofolioPersonalFragment.class.getSimpleName();

    private List<PromotionLink> cachedPromotionLinks;
    private Podcaster cachedPodcaster;

    private PortofolioPersonalViewMvc viewMvc;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMvc = new PortofolioPersonalViewMvcImpl(inflater, container);
        return viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        boolean isValidState = savedInstanceState != null
                && savedInstanceState.containsKey(PromotionLink.PROMOTION_LINKS_KEY)
                && savedInstanceState.containsKey(Podcaster.PODCASTER_KEY);

        if (isValidState) {
            cachedPodcaster = savedInstanceState.getParcelable(Podcaster.PODCASTER_KEY);
            cachedPromotionLinks = savedInstanceState.getParcelableArrayList(PromotionLink.PROMOTION_LINKS_KEY);
        }

        getLoaderManager().restartLoader(PROMOTION_LOADER_ID, null, this);
        getLoaderManager().restartLoader(PODCASTER_LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(PromotionLink.PROMOTION_LINKS_KEY, (ArrayList<? extends Parcelable>) cachedPromotionLinks);
        outState.putParcelable(Podcaster.PODCASTER_KEY, cachedPodcaster);
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case PROMOTION_LOADER_ID:
                return new AsyncTaskLoader<Object>(getContext()) {

                    @Override
                    protected void onStartLoading() {
                        if (cachedPromotionLinks != null) {
                            deliverResult(cachedPromotionLinks);
                        } else {
                            viewMvc.displayLoadingIndicator(true);
                            forceLoad();
                        }
                    }

                    @Nullable
                    @Override
                    public Object loadInBackground() {
                        //  TODO: Replace with real service.
                        DataRepository repo = new StaticFakeDataRepo();
                        return repo.fetchPromotionLinks(repo.getCurrentUserUid());
                    }
                };

            case PODCASTER_LOADER_ID:
                return new AsyncTaskLoader<Object>(getContext()) {

                    @Override
                    protected void onStartLoading() {
                        if (cachedPodcaster != null) {
                            deliverResult(cachedPodcaster);
                        } else {
                            forceLoad();
                        }
                    }

                    @Nullable
                    @Override
                    public Object loadInBackground() {
                        //  TODO: Replace with a real service
                        DataRepository repo = new StaticFakeDataRepo();
                        return repo.fetchPodcaster(repo.getCurrentUserUid());
                    }
                };

            default:
                throw new UnsupportedOperationException(TAG + ": unknown loader id.");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        if (data != null) {
            switch (loader.getId()) {
                case PROMOTION_LOADER_ID:
                    List<PromotionLink> list = (List<PromotionLink>) data;
                    if (list != null && list.size() > 0) {
                        viewMvc.displayLoadingIndicator(false);
                        viewMvc.bindPromotionLinks(cachedPromotionLinks = list);
                    }
                    break;

                case PODCASTER_LOADER_ID:
                    if (data instanceof Podcaster) {
                        cachedPodcaster = (Podcaster) data;
                        viewMvc.bindPodcasterName(cachedPodcaster.getUsername());
                        viewMvc.bindPodcastPoster(cachedPodcaster.getImageUrl());
                        viewMvc.bindPersonalStatement(cachedPodcaster.getPersonalStatement());
                    }
                    break;

                default:
                    throw new UnsupportedOperationException(TAG + ": unknown loader id.");
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    @Override
    public void save() {

    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }
}
