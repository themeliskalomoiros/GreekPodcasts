package gr.kalymnos.sk3m3l10.greekpodcasts.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class Podcast implements Parcelable{

    public static final String PODCASTS_KEY = "podcasts_key";
    public static final String PODCAST_KEY = "podcast_key";
    public static final String POSTER_KEY = "poster_key";

    private String title, categoryId, posterUrl, description, podcasterId, firebasePushId, episodesId;

    public Podcast() {
    }

    public Podcast(String title, String categoryId, String posterUrl, String description, String podcasterId, String episodesId) {
        this.title = title;
        this.categoryId = categoryId;
        this.posterUrl = posterUrl;
        this.description = description;
        this.podcasterId = podcasterId;
        this.episodesId = episodesId;
    }

    public Podcast(String title, String categoryId, String posterUrl, String description, String podcasterId, String firebasePushId, String episodesId) {
        this.title = title;
        this.categoryId = categoryId;
        this.posterUrl = posterUrl;
        this.description = description;
        this.podcasterId = podcasterId;
        this.firebasePushId = firebasePushId;
        this.episodesId = episodesId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getPodcasterId() {
        return podcasterId;
    }

    public String getFirebasePushId() {
        return firebasePushId;
    }

    public String getEpisodesId() {
        return episodesId;
    }

    public void setFirebasePushId(String firebasePushId) {
        this.firebasePushId = firebasePushId;
    }

    protected Podcast(Parcel in) {
        title = in.readString();
        categoryId = in.readString();
        posterUrl = in.readString();
        description = in.readString();
        podcasterId = in.readString();
        firebasePushId = in.readString();
        episodesId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(categoryId);
        dest.writeString(posterUrl);
        dest.writeString(description);
        dest.writeString(podcasterId);
        dest.writeString(firebasePushId);
        dest.writeString(episodesId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Podcast> CREATOR = new Creator<Podcast>() {
        @Override
        public Podcast createFromParcel(Parcel in) {
            return new Podcast(in);
        }

        @Override
        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };
}
