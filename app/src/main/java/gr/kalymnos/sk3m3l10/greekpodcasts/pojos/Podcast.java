package gr.kalymnos.sk3m3l10.greekpodcasts.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class Podcast implements Parcelable{

    public static final String PODCASTS_KEY = "podcasts_key";
    public static final String PODCAST_KEY = "podcast_key";
    public static final String POSTER_KEY = "poster_key";
    public static final String DESCRIPTION_KEY = "description key";
    public static final String LOCAL_DB_ID_KEY = "local db key";

    private String title, categoryId, posterUrl, description, podcasterId, firebasePushId, episodesId;
    private int localDbId;

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

    protected Podcast(Parcel in) {
        title = in.readString();
        categoryId = in.readString();
        posterUrl = in.readString();
        description = in.readString();
        podcasterId = in.readString();
        firebasePushId = in.readString();
        episodesId = in.readString();
        localDbId = in.readInt();
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

    public int getLocalDbId() {
        return localDbId;
    }

    public void setLocalDbId(int localDbId) {
        this.localDbId = localDbId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(categoryId);
        parcel.writeString(posterUrl);
        parcel.writeString(description);
        parcel.writeString(podcasterId);
        parcel.writeString(firebasePushId);
        parcel.writeString(episodesId);
        parcel.writeInt(localDbId);
    }
}
