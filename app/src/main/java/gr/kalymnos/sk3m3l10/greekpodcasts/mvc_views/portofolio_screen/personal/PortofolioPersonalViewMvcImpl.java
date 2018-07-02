package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public class PortofolioPersonalViewMvcImpl implements PortofolioPersonalViewMvc {

    private View rootView;
    private TextView nameTextView, statementTextView;
    private ImageView personalPic;
    private ImageButton editNameButton, editStatementButton, editPromotionButton;
    private RecyclerView promotionRecyclerView;

    public PortofolioPersonalViewMvcImpl(LayoutInflater inflater, ViewGroup parent){
        initializeViews(inflater, parent);
    }

    @Override
    public void bindPodcasterName(String name) {
        nameTextView.setText(name);
    }

    @Override
    public void bindPodcastPoster(Bitmap poster) {
        personalPic.setImageBitmap(poster);
    }

    @Override
    public void bindPromotionLinks(List<PromotionLink> promotionLinks) {

    }

    @Override
    public void setOnButtonsClickListener(OnButtonsClickListener listener) {

    }

    @Override
    public View getRootView() {
        return rootView;
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.portofolio_personal,parent,false);
        nameTextView = rootView.findViewById(R.id.name_textview);
        statementTextView = rootView.findViewById(R.id.personal_statement_textview);
        personalPic = rootView.findViewById(R.id.personal_pic_imageview);
        editNameButton = rootView.findViewById(R.id.edit_name_imagebutton);
        editStatementButton = rootView.findViewById(R.id.edit_personal_statement_imagebutton);
        editPromotionButton = rootView.findViewById(R.id.edit_promotion_imagebutton);
        promotionRecyclerView = rootView.findViewById(R.id.recycler_view);
    }
}
