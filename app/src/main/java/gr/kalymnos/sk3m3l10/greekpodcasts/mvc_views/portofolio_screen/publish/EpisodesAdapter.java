package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeHolder> {

    private Context context;
    private List<Episode> episodes;

    public EpisodesAdapter(Context context) {
        this.context = context;
    }

    public void addEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public EpisodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_episode_dark, parent, false);
        return new EpisodeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeHolder holder, int position) {
        holder.bindTitle(episodes.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        if (episodes != null) {
            return episodes.size();
        }
        return 0;
    }

    class EpisodeHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;

        EpisodeHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.episode_title);
        }

        void bindTitle(String title) {
            this.titleTextView.setText(title);
        }
    }
}
