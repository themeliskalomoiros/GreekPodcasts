package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.FakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen.PodcasterViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen.PodcasterViewMvc.OnPromotionLinkClickListener;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen.PodcasterViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.DateUtils;

public class PodcasterActivity extends AppCompatActivity implements OnPromotionLinkClickListener,
        LoaderManager.LoaderCallbacks<Object> {

    private static final int PODCASTER_LOADER_ID = 100;
    private static final int PROMOTION_LINKS_LOADER_ID = 200;
    private static final String TAG = PodcasterActivity.class.getSimpleName();

    private PodcasterViewMvc viewMvc;

    private List<PromotionLink> cachedPromotionLinks;
    private Podcaster cachedPodcaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewMvc = new PodcasterViewMvcImpl(LayoutInflater.from(this), null);
        this.viewMvc.setOnPromotionLinkClickListener(this);
        this.setSupportActionBar(this.viewMvc.getToolBar());
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setContentView(this.viewMvc.getRootView());
        this.getSupportLoaderManager().restartLoader(PODCASTER_LOADER_ID, null, this);
    }

    @Override
    public void onPromotionLinkClick(int position) {
        if (cachedPromotionLinks != null && cachedPromotionLinks.size() > 0) {
            Uri webpage = Uri.parse(cachedPromotionLinks.get(position).getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        //  TODO: Switch with a real service.
        DataRepository dataRepository = new StaticFakeDataRepo();

        switch (id) {
            case PODCASTER_LOADER_ID:
                return new AsyncTaskLoader<Object>(this) {

                    @Override
                    protected void onStartLoading() {
                        if (cachedPodcaster != null) {
                            this.deliverResult(cachedPodcaster);
                        } else {
                            viewMvc.displayLoading(true);
                            this.forceLoad();
                        }
                    }

                    @Nullable
                    @Override
                    public Object loadInBackground() {
                        String podcasterPushId = getIntent().getExtras()
                                .getString(Podcaster.PUSH_ID_KEY);
                        return dataRepository.fetchPodcaster(podcasterPushId);
                    }
                };

            case PROMOTION_LINKS_LOADER_ID:
                return new AsyncTaskLoader<Object>(this) {

                    @Override
                    protected void onStartLoading() {
                        if (cachedPromotionLinks != null) {
                            this.deliverResult(cachedPromotionLinks);
                        } else {
                            viewMvc.displayLoading(true);
                            this.forceLoad();
                        }
                    }

                    @Nullable
                    @Override
                    public Object loadInBackground() {
                        if (cachedPodcaster != null) {
                            return dataRepository.fetchPromotionLinks(cachedPodcaster.getPromotionLinksId());
                        }
                        return null;
                    }
                };

            default:
                throw new IllegalArgumentException(TAG + " " + id + " does not belong to a Loader.");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        viewMvc.displayLoading(false);
        if (data != null) {

            //  This data can be a Podcaster object or a List<PromotionLink>'s,
            //  which belong to the podcaster anyway...
            switch (loader.getId()) {
                case PODCASTER_LOADER_ID:
                    if (data instanceof Podcaster) {
                        this.cachedPodcaster = (Podcaster) data;
                        this.viewMvc.bindPodcasterName(this.cachedPodcaster.getUsername());
                        this.viewMvc.bindJoinedDate(this.getString(R.string.joined_date_prefix)
                                + DateUtils.getJoinedDate(cachedPodcaster.getJoinedDate(), this.getResources()));
                        this.viewMvc.bindPersonalStatement(this.cachedPodcaster.getPersonalStatement());
                        this.viewMvc.bindPodcasterImageUrl(this.cachedPodcaster.getImageUrl());

                        //  Now that we have fetched successfully the podcaster let's quiry its
                        //  promotion links (because we have the Podcaster's pushId
                        this.getSupportLoaderManager().restartLoader(PROMOTION_LINKS_LOADER_ID, null, this);
                    } else {
                        throw new IllegalArgumentException(TAG + ": Fetched data is not a podcaster or its null.");
                    }
                    break;
                case PROMOTION_LINKS_LOADER_ID:
                    if (data != null) {
                        List<PromotionLink> tempCachedList = (List<PromotionLink>) data;
                        if (tempCachedList != null && tempCachedList.size() > 0) {
                            this.viewMvc.bindPromotionLinks(cachedPromotionLinks = tempCachedList);
                        }

                    }
                    break;
                default:
                    throw new IllegalArgumentException(TAG + " " + loader.getId() + " does not belong to a Loader.");
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }
}
