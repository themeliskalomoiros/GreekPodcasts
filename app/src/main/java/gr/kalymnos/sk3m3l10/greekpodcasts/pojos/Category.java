package gr.kalymnos.sk3m3l10.greekpodcasts.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/*  Podcast Category    */
public class Category implements Parcelable {
    public static final String CATEGORIES_KEY = "categories_key";
    public static final String CATEGORY_KEY = "category_key";

    private String title, explanation, imageUrl, firebasePushId;

    public Category() {
    }

    public Category(String title, String explanation, String imageUrl, String firebasePushId) {
        this.title = title;
        this.explanation = explanation;
        this.imageUrl = imageUrl;
        this.firebasePushId = firebasePushId;
    }

    public Category(String title, String explanation, String imageUrl) {
        this.title = title;
        this.explanation = explanation;
        this.imageUrl = imageUrl;
    }

    protected Category(Parcel in) {
        title = in.readString();
        explanation = in.readString();
        imageUrl = in.readString();
        firebasePushId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(explanation);
        dest.writeString(imageUrl);
        dest.writeString(firebasePushId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public static String getCategoriesKey() {
        return CATEGORIES_KEY;
    }

    public String getTitle() {
        return title;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFirebasePushId() {
        return firebasePushId;
    }

    public void setFirebasePushId(String firebasePushId) {
        this.firebasePushId = firebasePushId;
    }
}
