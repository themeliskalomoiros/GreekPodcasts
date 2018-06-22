package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public class PodcasterViewMvcImpl implements PodcasterViewMvc {

    private View rootView;
    private TextView username, joinedDate, personalStatement;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private PromotionLinksAdapter adapter;
    private ImageView podcasterImage;
    private ProgressBar progressBar;

    public PodcasterViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    @Override
    public void bindPodcasterName(String name) {
        if (username != null)
            username.setText(name);
    }

    @Override
    public void bindJoinedDate(String date) {
        if (joinedDate != null)
            joinedDate.setText(date);
    }

    @Override
    public void bindPersonalStatement(String personalStatement) {
        if (this.personalStatement != null)
            this.personalStatement.setText(personalStatement);
    }

    @Override
    public void bindPodcasterImageUrl(String url) {
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_headset_black_light_148dp)
                .error(R.drawable.ic_error_black_light_148dp)
                .into(this.podcasterImage);
    }

    @Override
    public Toolbar getToolBar() {
        return this.toolbar;
    }

    @Override
    public void displayLoading(boolean display) {
        if (display){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void bindPromotionLinks(List<PromotionLink> promotionLinks) {
        adapter.addPromotionLinks(promotionLinks);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setOnPromotionLinkClickListener(OnPromotionLinkClickListener listener) {
        adapter.setOnPromotionLinkClickListener(listener);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    private boolean onLand() {
        if (username == null) {
            return true;
        }
        return false;
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.activity_podcaster, parent, false);
        username = rootView.findViewById(R.id.podcaster_username_textview);
        joinedDate = rootView.findViewById(R.id.joined_textview);
        personalStatement = rootView.findViewById(R.id.personal_statement_textview);
        toolbar = rootView.findViewById(R.id.toolbar);
        podcasterImage = rootView.findViewById(R.id.podcaster_picture);
        progressBar = rootView.findViewById(R.id.pb_loading_indicator);
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new PromotionLinksAdapter(this.rootView.getContext());
        recyclerView.setAdapter(adapter);
    }
}
