package gr.kalymnos.sk3m3l10.greekpodcasts.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class PromotionLink implements Parcelable{
    public static final String PROMOTION_LINKS_KEY = "promotion links key";

    /*  Promotion Link is an object that the Podcaster defines to promote himself.
     *   Example: The title could be "Support me on Patreon" and the url "www.patreon.com/user5" */

    private String title, url;

    public PromotionLink() {
    }

    public PromotionLink(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    protected PromotionLink(Parcel in) {
        title = in.readString();
        url = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(url);
    }
}
