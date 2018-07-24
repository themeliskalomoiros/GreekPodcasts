package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.podcaster_screen.PodcasterViewMvc.OnPromotionLinkClickListener;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public class PromotionLinksAdapter extends RecyclerView.Adapter<PromotionLinksAdapter.PromotionLinkHolder> {

    private OnPromotionLinkClickListener listener;
    private List<PromotionLink> promotionLinks;
    private Context context;

    public PromotionLinksAdapter(Context context) {
        this.context = context;
    }

    public void setOnPromotionLinkClickListener(OnPromotionLinkClickListener listener) {
        this.listener = listener;
    }

    public void addPromotionLinks(List<PromotionLink> promotionLinks) {
        this.promotionLinks = promotionLinks;
    }

    @NonNull
    @Override
    public PromotionLinkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.list_item_promotion_link, parent, false);
        return new PromotionLinkHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionLinkHolder holder, int position) {
        if (promotionLinks != null && promotionLinks.size() > 0)
            holder.bindPromotionLink(promotionLinks.get(position));
    }

    @Override
    public int getItemCount() {
        if (promotionLinks != null && promotionLinks.size() > 0) {
            return promotionLinks.size();
        }
        return 0;
    }

    public List<PromotionLink> getPromotionLinks() {
        return promotionLinks;
    }

    class PromotionLinkHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title, url;

        public PromotionLinkHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.promotion_msg_textview);
            url = itemView.findViewById(R.id.promotion_link_textview);
        }

        void bindPromotionLink(PromotionLink promotionLink) {
            if (promotionLink != null) {
                if (title != null) {
                    title.setText(promotionLink.getTitle());
                }
                if (url != null) {
                    url.setText(promotionLink.getUrl());
                }
            }
        }

        @Override
        public void onClick(View view) {
            if (listener != null)
                listener.onPromotionLinkClick(getAdapterPosition());
        }
    }
}
