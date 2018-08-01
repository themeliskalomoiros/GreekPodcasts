package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen.PodcasterViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen.PodcasterViewMvc.OnPromotionLinkClickListener;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen.PodcasterViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.DateUtils;

public class PodcasterActivity extends AppCompatActivity implements OnPromotionLinkClickListener {

    private static final String TAG = PodcasterActivity.class.getSimpleName();

    private PodcasterViewMvc viewMvc;

    private List<PromotionLink> cachedPromotionLinks;
    private Podcaster cachedPodcaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewMvc();
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Podcaster.PODCASTER_KEY)) {
                cachedPodcaster = savedInstanceState.getParcelable(Podcaster.PODCASTER_KEY);
            }

            if (savedInstanceState.containsKey(PromotionLink.PROMOTION_LINKS_KEY)) {
                cachedPromotionLinks = savedInstanceState.getParcelableArrayList(PromotionLink.PROMOTION_LINKS_KEY);
            }
        } else {
            fetchData();
        }
    }

    private void fetchData() {
        viewMvc.displayLoading(true);
        String podcasterPushId = getIntent().getExtras().getString(Podcaster.PUSH_ID_KEY);
        FirebaseDatabase.getInstance().getReference()
                .child(ChildNames.PODCASTERS)
                .child(podcasterPushId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Podcaster podcaster = dataSnapshot.getValue(Podcaster.class);
                        if (podcaster != null) {
                            cachedPodcaster = podcaster;
                            bindPodcasterDataToUi();

                            //  Now that we have fetched successfully the podcaster let's quiry its
                            //  promotion links (because we have the Podcaster's pushId
                            fetchPromotionLinks();
                        }
                    }

                    private void fetchPromotionLinks() {
                        FirebaseDatabase.getInstance().getReference()
                                .child(ChildNames.PROMOTION_LINKS)
                                .child(podcasterPushId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        viewMvc.displayLoading(false);
                                        List<PromotionLink> tempPromotionLinkList = new ArrayList<>();
                                        for (DataSnapshot promotionLinkSnapshot : dataSnapshot.getChildren()) {
                                            PromotionLink promotionLink = promotionLinkSnapshot.getValue(PromotionLink.class);
                                            if (promotionLink != null) {
                                                tempPromotionLinkList.add(promotionLink);
                                            }
                                        }

                                        if (tempPromotionLinkList.size() > 0) {
                                            viewMvc.bindPromotionLinks(cachedPromotionLinks = tempPromotionLinkList);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    private void bindPodcasterDataToUi() {
                        viewMvc.bindPodcasterName(cachedPodcaster.getUsername());
                        viewMvc.bindJoinedDate(getString(R.string.joined_date_prefix)
                                + DateUtils.getJoinedDate(cachedPodcaster.getJoinedDate(), getResources()));
                        viewMvc.bindPersonalStatement(cachedPodcaster.getPersonalStatement());
                        viewMvc.bindPodcasterImageUrl(cachedPodcaster.getImageUrl());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cachedPodcaster != null) {
            outState.putParcelable(Podcaster.PODCASTER_KEY, cachedPodcaster);
        }
        if (cachedPromotionLinks != null && cachedPromotionLinks.size() > 0) {
            outState.putParcelableArrayList(PromotionLink.PROMOTION_LINKS_KEY, (ArrayList<? extends Parcelable>) cachedPromotionLinks);
        }
    }

    private void initializeViewMvc() {
        this.viewMvc = new PodcasterViewMvcImpl(LayoutInflater.from(this), null);
        this.viewMvc.setOnPromotionLinkClickListener(this);
        this.setSupportActionBar(this.viewMvc.getToolBar());
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setContentView(this.viewMvc.getRootView());
    }
}
