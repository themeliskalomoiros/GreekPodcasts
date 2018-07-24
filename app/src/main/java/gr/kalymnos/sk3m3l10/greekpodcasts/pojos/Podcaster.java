package gr.kalymnos.sk3m3l10.greekpodcasts.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class Podcaster implements Parcelable {

    public static final String PUSH_ID_KEY = "podcaster push id  key";
    public static final String PODCASTER_KEY = "podcaster key";

    public static final String FIELD_NAME_EMAIL = "email";
    public static final String FIELD_NAME_USERNAME = "username";
    public static final String FIELD_NAME_PERSONAL_STATEMENT = "personalStatement";
    public static final String FIELD_NAME_IMAGEURL = "imageUrl";

    private String email, username, personalStatement, imageUrl;
    private long joinedDate;

    public Podcaster() {
    }

    public Podcaster(String email, String username, String personalStatement, String imageUrl, long joinedDate) {
        this.email = email;
        this.username = username;
        this.personalStatement = personalStatement;
        this.imageUrl = imageUrl;
        this.joinedDate = joinedDate;
    }

    protected Podcaster(Parcel in) {
        email = in.readString();
        username = in.readString();
        personalStatement = in.readString();
        imageUrl = in.readString();
        joinedDate = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(personalStatement);
        dest.writeString(imageUrl);
        dest.writeLong(joinedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Podcaster> CREATOR = new Creator<Podcaster>() {
        @Override
        public Podcaster createFromParcel(Parcel in) {
            return new Podcaster(in);
        }

        @Override
        public Podcaster[] newArray(int size) {
            return new Podcaster[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonalStatement() {
        return personalStatement;
    }

    public void setPersonalStatement(String personalStatement) {
        this.personalStatement = personalStatement;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(long joinedDate) {
        this.joinedDate = joinedDate;
    }
}
