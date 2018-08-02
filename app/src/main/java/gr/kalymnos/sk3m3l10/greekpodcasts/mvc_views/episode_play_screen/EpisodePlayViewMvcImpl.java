package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.episode_play_screen;

import android.graphics.Bitmap;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class EpisodePlayViewMvcImpl implements EpisodePlayViewMvc {

    //  TODO:   Missing Seekbar functionality!!!

    private View rootView;
    private TextView podcasterLabel, episodeTitle, playbackPosition, playbackDuration;
    private ImageView star, download, info, play, next, previous, pause, poster;
    private SeekBar seekBar;
    private Toolbar toolbar;

    public EpisodePlayViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        initializeViews(inflater, parent);
    }

    @Override
    public int getDownloadStartedMessage() {
        return R.string.download_started_message;
    }

    @Override
    public int getDownloadCompletedMessage() {
        return R.string.download_completed;
    }

    @Override
    public void bindPoster(String url) {
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_headset_black_light_148dp)
                .error(R.drawable.ic_error_black_light_148dp)
                .into(this.poster);
    }

    @Override
    public void drawStarButton() {
        star.setImageResource(R.drawable.ic_star_pink_40dp);
    }

    @Override
    public void unDrawStarButton() {
        star.setImageResource(R.drawable.ic_star_white_40dp);
    }

    @Override
    public void drawDownloadButton() {
        download.setImageResource(R.drawable.ic_delete_forever_pink_40dp);
    }

    @Override
    public void unDrawDownloadButton() {
        download.setImageResource(R.drawable.ic_file_download_white_40dp);
    }

    @Override
    public void bindPoster(Bitmap bitmap) {
        poster.setImageBitmap(bitmap);
    }

    @Override
    public void bindPodcaster(String name) {
        //  First underline the text and then set it to the textview
        SpannableString content = new SpannableString(name);
        content.setSpan(new UnderlineSpan(),0,content.length(),0);
        this.podcasterLabel.setText(content);
    }

    @Override
    public void bindEpisodeTitle(String title) {
        this.episodeTitle.setText(title);
    }

    @Override
    public void bindPlaybackPosition(String position) {
        this.playbackPosition.setText(position);
    }

    @Override
    public void bindPlaybackDuration(String duration) {
        this.playbackDuration.setText(duration);
    }

    @Override
    public void displayPlayButton(boolean display) {
        if (display) {
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.INVISIBLE);
        } else {
            play.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void enableSeekBar(boolean enable) {
        this.seekBar.setEnabled(enable);
    }

    @Override
    public int getSeekBarProgress() {
        if (seekBar != null) {
            return seekBar.getProgress();
        }
        return 0;
    }

    @Override
    public void resetSeekBarProgress() {
        if (seekBar != null) {
            seekBar.setProgress(0);
        }
    }

    @Override
    public void bindSeekBarMax(int max) {
        this.seekBar.setMax(max);
    }

    @Override
    public void disableTransportControls(boolean disable) {
        if (disable) {
            play.setEnabled(false);
            pause.setEnabled(false);
            next.setEnabled(false);
            previous.setEnabled(false);
            seekBar.setEnabled(false);
        } else {
            play.setEnabled(true);
            pause.setEnabled(true);
            next.setEnabled(true);
            previous.setEnabled(true);
            seekBar.setEnabled(true);
        }
    }

    @Override
    public void bindSeekBarProgress(int progress) {
        this.seekBar.setProgress(progress);
    }

    @Override
    public void setSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        if (listener != null)
            this.seekBar.setOnSeekBarChangeListener(listener);
    }

    @Override
    public Toolbar getToolbar() {
        return this.toolbar;
    }

    @Override
    public void setOnActionButtonsClickListener(OnActionButtonsClickListener listener) {
        if (listener != null) {
            this.star.setOnClickListener(view -> listener.onStarClick());
            this.download.setOnClickListener(view -> listener.onDownloadClick());
            this.info.setOnClickListener(view -> listener.onInfoClick());
        }
    }

    @Override
    public void setOnTransportControlsClickListener(OnTransportControlsClickListener listener) {
        if (listener != null) {
            this.play.setOnClickListener(view -> listener.onPlayButtonClick());
            this.pause.setOnClickListener(view -> listener.onPauseButtonClick());
            this.next.setOnClickListener(view -> listener.onSkipToNextButtonClick());
            this.previous.setOnClickListener(view -> listener.onSkipToPreviousButtonClick());
        }
    }

    @Override
    public void setOnPodcasterClickListener(OnPodcasterClickListener listener) {
        this.podcasterLabel.setOnClickListener(label -> {
            if (listener != null)
                listener.onPodcasterClick();
        });
    }

    @Override
    public int getInfoContainerId() {
        return R.id.about_podcast_container;
    }

    @Override
    public View getRootView() {
        return this.rootView;
    }

    private void initializeViews(LayoutInflater inflater, ViewGroup parent) {
        this.rootView = inflater.inflate(R.layout.activity_episode, parent, false);
        this.podcasterLabel = this.rootView.findViewById(R.id.podcaster_textView);
        this.episodeTitle = this.rootView.findViewById(R.id.episode_title_textview);
        this.playbackPosition = this.rootView.findViewById(R.id.play_playback_current_position);
        this.playbackDuration = this.rootView.findViewById(R.id.play_playback_duration);
        this.poster = this.rootView.findViewById(R.id.poster_imageview);
        this.star = this.rootView.findViewById(R.id.star_imageview);
        this.download = this.rootView.findViewById(R.id.download_imageview);
        this.info = this.rootView.findViewById(R.id.info_imageview);
        this.play = this.rootView.findViewById(R.id.player_play);
        this.pause = this.rootView.findViewById(R.id.player_pause);
        this.previous = this.rootView.findViewById(R.id.player_previous);
        this.next = this.rootView.findViewById(R.id.player_next);
        this.seekBar = this.rootView.findViewById(R.id.player_seekbar);
        this.toolbar = this.rootView.findViewById(R.id.toolbar);
    }
}
