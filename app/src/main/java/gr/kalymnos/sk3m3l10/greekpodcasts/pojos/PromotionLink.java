package gr.kalymnos.sk3m3l10.greekpodcasts.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class PromotionLink implements Parcelable{
    public static final String PROMOTION_LINKS_KEY = "promotion links key";

    /*  Promotion Link is an object that the Podcaster defines to promote himself.
     *   Example: The title could be "Support me on Patreon" and the url "www.patreon.com/user5" */

    private String title, url, firebasePushId, podcasterId;

    public PromotionLink() {
    }

    public PromotionLink(String title, String url, String firebasePushId, String podcasterId) {
        this.title = title;
        this.url = url;
        this.firebasePushId = firebasePushId;
        this.podcasterId = podcasterId;
    }

    public PromotionLink(String title, String url, String podcasterId) {
        this.title = title;
        this.url = url;
        this.podcasterId = podcasterId;
    }

    protected PromotionLink(Parcel in) {
        title = in.readString();
        url = in.readString();
        firebasePushId = in.readString();
        podcasterId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(firebasePushId);
        dest.writeString(podcasterId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PromotionLink> CREATOR = new Creator<PromotionLink>() {
        @Override
        public PromotionLink createFromParcel(Parcel in) {
            return new PromotionLink(in);
        }

        @Override
        public PromotionLink[] newArray(int size) {
            return new PromotionLink[size];
        }
    };

    public void setFirebasePushId(String firebasePushId) {
        this.firebasePushId = firebasePushId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getFirebasePushId() {
        return firebasePushId;
    }

    public String getPodcasterId() {
        return podcasterId;
    }
}
