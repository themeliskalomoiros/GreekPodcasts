package gr.kalymnos.sk3m3l10.greekpodcasts.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class Episode implements Parcelable {
    public static final String EPISODES_KEY = "episodes_key";
    public static final String EPISODE_KEY = "episode_key";
    public static final String TITLE_KEY = "title-key";
    public static final String INDEX_KEY = "index_key";
    public static final String FIREBASE_ID_KEY = "firebase_id key";

    private String title, url, firebasePushId;
    private int minutes, seconds;
    private long dateMilli;

    public Episode() {
    }

    public Episode(String title, String url, int minutes, int seconds, long dateMilli) {
        this.title = title;
        this.url = url;
        this.minutes = minutes;
        this.seconds = seconds;
        this.dateMilli = dateMilli;
    }

    public Episode(String title, String url, String firebasePushId, int minutes, int seconds, long dateMilli) {
        this.title = title;
        this.url = url;
        this.firebasePushId = firebasePushId;
        this.minutes = minutes;
        this.seconds = seconds;
        this.dateMilli = dateMilli;
    }

    protected Episode(Parcel in) {
        title = in.readString();
        url = in.readString();
        firebasePushId = in.readString();
        minutes = in.readInt();
        seconds = in.readInt();
        dateMilli = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(firebasePushId);
        dest.writeInt(minutes);
        dest.writeInt(seconds);
        dest.writeLong(dateMilli);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
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

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public long getDateMilli() {
        return dateMilli;
    }
}
