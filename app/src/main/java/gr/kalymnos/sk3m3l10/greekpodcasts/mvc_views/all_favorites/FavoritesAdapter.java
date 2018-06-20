package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.PodcastsAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_favorites.AllFavoritesViewMvc.OnPodcastItemLongClickListener;

public class FavoritesAdapter extends PodcastsAdapter {

    private OnPodcastItemLongClickListener onPodcastItemLongClickListener;

    public FavoritesAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public PodcastsAdapter.PodcastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View itemView = inflater.inflate(R.layout.list_item_podcast,parent,false);
        return new PodcastHolder(itemView);
    }

    public void setOnPodcastItemLongClickListener(OnPodcastItemLongClickListener onPodcastItemLongClickListener) {
        this.onPodcastItemLongClickListener = onPodcastItemLongClickListener;
    }

    class PodcastHolder extends PodcastsAdapter.PodcastHolder implements View.OnLongClickListener {

        public PodcastHolder(View itemView) {
            super(itemView);
            this.itemView.setOnLongClickListener(this);
        }

        @Override
        public void bindPodcast(Podcast podcast) {
            this.title.setText(podcast.getTitle());
            Picasso.get().load(podcast.getPosterUrl()).placeholder(IC_HEADSET).error(IC_ERROR).into(poster);
            //  Here all the podcast items are favorites, so show the star
            this.star.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean onLongClick(View v) {
            if (onPodcastItemLongClickListener != null) {
                onPodcastItemLongClickListener.onPodcastItemLongClick(this.getAdapterPosition());
            }
            return true;
        }
    }
}
