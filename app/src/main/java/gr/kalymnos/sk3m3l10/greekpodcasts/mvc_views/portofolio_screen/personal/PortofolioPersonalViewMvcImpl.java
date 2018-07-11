package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal;

import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen.PromotionLinksAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public class PortofolioPersonalViewMvcImpl implements PortofolioPersonalViewMvc {

    private View rootView;
    private TextView nameTextView, statementTextView, imageHintTextView, imageFileNameTextView;
    private ImageView personalPic;
    private ImageButton editNameButton, editStatementButton, editPromotionButton;
    private ProgressBar progressBar;
    private RecyclerView promotionRecyclerView;
    private PromotionLinksAdapter adapter;

    public PortofolioPersonalViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    @Override
    public void bindImage(Uri uri) {
        Picasso.get().load(uri)
                .placeholder(R.drawable.ic_headset_black_light_148dp)
                .error(R.drawable.ic_error_black_light_148dp)
                .into(personalPic);
    }

    @Override
    public void bindPodcasterName(String name) {
        nameTextView.setText(name);
    }

    @Override
    public void bindImage(String url) {
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_headset_black_light_148dp)
                .error(R.drawable.ic_error_black_light_148dp)
                .into(personalPic);
    }

    @Override
    public void bindPromotionLinks(List<PromotionLink> promotionLinks) {
        if (promotionLinks != null && promotionLinks.size() > 0) {
            adapter.addPromotionLinks(promotionLinks);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setOnViewsClickListener(OnViewsClickListener listener) {
        if (listener != null) {
            personalPic.setOnClickListener(pic -> listener.onImageClick());
            editStatementButton.setOnClickListener(button -> listener.onEditPersonalStatementClick());
            editNameButton.setOnClickListener(button -> listener.onEditPodcasterName());
            editPromotionButton.setOnClickListener(button -> listener.onEditPromotionLinkClick());
        }
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
    public void bindPersonalStatement(String statement) {
        statementTextView.setText(statement);
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
    public void bindImageFileName(String fileName) {
        imageFileNameTextView.setText(fileName);
    }

    @Override
    public int getNameDialogTitleRes() {
        return R.string.insert_podcaster_name_title;
    }

    @Override
    public int getPersonalStatementDialogTitleRes() {
        return R.string.insert_personal_statement_title;
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.portofolio_personal, parent, false);
        nameTextView = rootView.findViewById(R.id.name_textview);
        statementTextView = rootView.findViewById(R.id.personal_statement_textview);
        personalPic = rootView.findViewById(R.id.personal_pic_imageview);
        editNameButton = rootView.findViewById(R.id.edit_name_imagebutton);
        editStatementButton = rootView.findViewById(R.id.edit_personal_statement_imagebutton);
        editPromotionButton = rootView.findViewById(R.id.edit_promotion_imagebutton);
        progressBar = rootView.findViewById(R.id.pb_loading_indicator);
        imageHintTextView = rootView.findViewById(R.id.click_to_import_new_pic_hint);
        imageFileNameTextView = rootView.findViewById(R.id.chosen_image_file_name);
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        promotionRecyclerView = rootView.findViewById(R.id.recycler_view);
        adapter = new PromotionLinksAdapter(rootView.getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        promotionRecyclerView.setLayoutManager(layoutManager);
        promotionRecyclerView.setHasFixedSize(true);
        promotionRecyclerView.setAdapter(adapter);
    }
}
