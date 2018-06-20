package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.local_database.UserMetadataContract.PodcastWatchedEntry;
import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_podcasts.AllPodcastsViewMvc.OnPodcastItemClickListener;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public class PodcastsAdapter extends RecyclerView.Adapter<PodcastsAdapter.PodcastHolder> {

    protected Context context;
    protected List<Podcast> podcasts = null;
    protected OnPodcastItemClickListener onPodcastItemClickListener;

    public PodcastsAdapter(@NonNull Context context) {
        this.context = context;
    }

    public void addPodcasts(List<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public void setOnPodcastItemClickListener(OnPodcastItemClickListener onPodcastItemClickListener) {
        this.onPodcastItemClickListener = onPodcastItemClickListener;
    }

    @NonNull
    @Override
    public PodcastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View itemView = inflater.inflate(R.layout.list_item_podcast, parent, false);
        return new PodcastHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PodcastHolder holder, int position) {
        if (this.podcasts != null || this.podcasts.size() > 0) {
            Podcast podcast = this.podcasts.get(position);
            holder.bindPodcast(podcast);
        }
    }

    @Override
    public int getItemCount() {
        if (this.podcasts != null) {
            return this.podcasts.size();
        }
        return 0;
    }

    public class PodcastHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected static final int IC_HEADSET = R.drawable.ic_headset_black_light_148dp;
        protected static final int IC_ERROR = R.drawable.ic_error_black_light_148dp;
        protected final String TAG = PodcastHolder.class.getCanonicalName();

        protected TextView title;
        protected ImageView poster, star;

        public PodcastHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.list_item_podcast_title);
            this.poster = itemView.findViewById(R.id.list_item_podcast_image);
            this.star = itemView.findViewById(R.id.list_item_podcast_star);
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onPodcastItemClickListener != null) {
                onPodcastItemClickListener.onItemPodcastClick(this.getAdapterPosition());
            }
        }

        public void bindPodcast(Podcast podcast) {
            this.title.setText(podcast.getTitle());
            Picasso.get().load(podcast.getPosterUrl()).placeholder(IC_HEADSET).error(IC_ERROR).into(poster);
            // This task will figure out from the local database if the podcast is starred
            // and if true it will light a star
            new StarredTask().execute(podcast.getFirebasePushId());
        }

        private class StarredTask extends AsyncTask<String, Void, Boolean> {

            @Override
            protected Boolean doInBackground(String... firebasePushIds) {
                int starred = 0;    /*  Which means false */
                String firebasePushId = firebasePushIds[0];

                if (firebasePushId == null) {
                    throw new UnsupportedOperationException(TAG + ": need a firebase push id to calculate if this podcast is starred in local database.");
                }

                Context context = itemView.getContext();

                String selection = PodcastWatchedEntry.COLUMN_NAME_FIREBASE_PUSH_ID + "=?";
                String[] selectionArgs = new String[]{firebasePushId};
                Cursor cursor = context.getContentResolver().query(PodcastWatchedEntry.CONTENT_URI, null, selection, selectionArgs, null);

                /*  There should be only one item in the cursor with this also unique firebase push id.
                    The justification for that lies on how podcast meta-data is saved in the local database.

                    ==  The HOW  ==
                    Podcasts are fetched from Firebase. When user clicks a podcast item then the system
                    compares the fetched podcasts pushId with any of those store in the database. If there
                    is one (and there should be only one) then nothing happens, otherwise a new row is inserted
                    into the local database with the new pushId. */
                if (cursor != null && cursor.getCount() == 1) {
                    //  The podcast was saved (watched/clicked by user) in the database before.
                    cursor.moveToNext();
                    int starredIndex = cursor.getColumnIndex(PodcastWatchedEntry.COLUMN_NAME_STARRED);
                    starred = cursor.getInt(starredIndex);
                }
                return starred > 0 ? true : false;
            }

            @Override
            protected void onPostExecute(Boolean isStarred) {
                if (isStarred) {
                    star.setVisibility(View.VISIBLE);
                } else {
                    star.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
