package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_episodes;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.DateUtils;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_episodes.AllEpisodesViewMvc.OnEpisodeClickListener;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_episodes.AllEpisodesViewMvc.OnPopUpMenuClickListener;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeHolder> {

    private Context context;
    private List<MediaBrowserCompat.MediaItem> episodes;

    private OnPopUpMenuClickListener onPopUpMenuClickListener;
    private OnEpisodeClickListener onEpisodeClickListener;

    //  We cache this viewHolder just to call EpisodeHolder.markSelectionView() elsewhere.
    public EpisodeHolder cachedViewHolder;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public EpisodesAdapter(Context context) {
        this.context = context;
    }

    public void addEpisodes(List<MediaBrowserCompat.MediaItem> episodes) {
        this.episodes = episodes;
    }

    public void setOnPopUpMenuClickListener(OnPopUpMenuClickListener onPopUpMenuClickListener) {
        this.onPopUpMenuClickListener = onPopUpMenuClickListener;
    }

    public void setOnEpisodeClickListener(OnEpisodeClickListener onEpisodeClickListener) {
        this.onEpisodeClickListener = onEpisodeClickListener;
    }

    @NonNull
    @Override
    public EpisodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View itemView = inflater.inflate(R.layout.list_item_episode, parent, false);
        return cachedViewHolder = new EpisodeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeHolder holder, int position) {
        if (this.episodes != null && this.episodes.size() > 0) {
            holder.bindViews(episodes.get(position));
            holder.itemView.setSelected(selectedPosition == position);
        }
    }

    @Override
    public int getItemCount() {
        if (this.episodes != null) {
            return this.episodes.size();
        }
        return 0;
    }

    class EpisodeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dateTextView, titleTextView, durationTextView;
        ImageView popUpMenuImageView;

        public EpisodeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            initializeViews(itemView);
        }

        void bindViews(MediaBrowserCompat.MediaItem item) {
            this.dateTextView.setText(DateUtils.getStringDateFromMilli(item.getDescription().getExtras().getLong(Episode.DATE_KEY),
                    context.getResources(), useFullTextMonth()));
            this.titleTextView.setText(item.getDescription().getTitle());
            this.durationTextView.setText(String.format("%d:%d", item.getDescription().getExtras().getInt(Episode.MINUTES_KEY),
                    item.getDescription().getExtras().getInt(Episode.SECONDS_KEY)));
        }

        private boolean useFullTextMonth() {
            int screenOrientation = context.getResources().getConfiguration().orientation;
            return screenOrientation == Configuration.ORIENTATION_LANDSCAPE ? true : false;
        }

        private void initializeViews(View itemView) {
            this.dateTextView = itemView.findViewById(R.id.date_textview);
            this.titleTextView = itemView.findViewById(R.id.title_textview);
            this.durationTextView = itemView.findViewById(R.id.duration_textview);
            //  TODO:   Set this view a click listener, to pop up a menu.
            this.popUpMenuImageView = itemView.findViewById(R.id.popUp_menu_imageview);
            this.popUpMenuImageView.setOnClickListener(v -> {
                if (onPopUpMenuClickListener != null)
                    onPopUpMenuClickListener.onPopUpMenuClick(this.getAdapterPosition());
            });
        }

        @Override
        public void onClick(View v) {
            //  TODO: Implement what the episode will do if clicked.
            if (onEpisodeClickListener != null)
                onEpisodeClickListener.onEpisodeClick(this.getAdapterPosition());

            markSelectionView();
        }

        private void markSelectionView() {
            //  This method selects one item (a res/drawable/selector is used as a background)
            notifyItemChanged(selectedPosition);
            selectedPosition = getLayoutPosition();
            notifyItemChanged(selectedPosition);
        }

        public void markSelectionView(int positionToMark) {
            notifyItemChanged(selectedPosition);
            selectedPosition = positionToMark;
            notifyItemChanged(selectedPosition);
        }
    }
}
