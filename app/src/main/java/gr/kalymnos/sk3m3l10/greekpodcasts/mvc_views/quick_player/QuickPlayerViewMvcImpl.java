package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.quick_player;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class QuickPlayerViewMvcImpl implements QuickPlayerViewMvc {

    private View rootView;
    private ImageView poster, play, pause;
    private TextView title;

    public QuickPlayerViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    @Override
    public void setOnTransportControlsClickListener(OnTransportControlsClickListener listener) {
        if (listener != null) {

            this.play.setOnClickListener(button -> {
                listener.onPlayButtonClick();
            });

            this.pause.setOnClickListener(button -> {
                listener.onPauseButtonClick();
            });
        }

    }

    @Override
    public void setOnRootClickListener(OnQuickPlayerClickListener listener) {
        this.rootView.setOnClickListener(view -> {
            if (listener != null)
                listener.onQuickPlayerClick();
        });
    }

    @Override
    public void bindEpisodeTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void bindPodcastTitleColor(int color) {
        this.title.setTextColor(color);
    }


    @Override
    public void displayPlayButton(boolean display) {
        if (display) {
            this.play.setVisibility(View.VISIBLE);
            this.pause.setVisibility(View.INVISIBLE);
        } else {
            this.play.setVisibility(View.INVISIBLE);
            this.pause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void bindBackgroundColor(int color) {
        this.rootView.setBackgroundColor(color);
    }

    @Override
    public void disableRoot(boolean disable) {
        if (disable){
            rootView.setEnabled(false);
        }else{
            rootView.setEnabled(true);
        }
    }

    @Override
    public Bitmap getPosterBitmap() {
        if (poster!=null){
            return ((BitmapDrawable)poster.getDrawable()).getBitmap();
        }
        return null;
    }

    @Override
    public void bindPodcastPoster(String url) {
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_headset_black_light_148dp)
                .error(R.drawable.ic_error_black_light_148dp)
                .into(this.poster);
    }

    @Override
    public void bindPodcastPoster(Bitmap bitmap) {
        poster.setImageBitmap(bitmap);
    }

    @Override
    public void disableTransportControls(boolean disable) {
        if (disable){
            play.setEnabled(false);
            pause.setEnabled(false);
        }else{
            play.setEnabled(true);
            play.setEnabled(true);
        }
    }

    @Override
    public View getRootView() {
        return this.rootView;
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        this.rootView = inflater.inflate(R.layout.quick_media_player, parent, false);
        this.poster = this.rootView.findViewById(R.id.quick_player_poster);
        this.play = this.rootView.findViewById(R.id.player_play);
        this.pause = this.rootView.findViewById(R.id.player_pause);
        this.title = this.rootView.findViewById(R.id.quick_player_title);
    }
}
