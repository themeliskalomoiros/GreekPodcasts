package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

public class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    private BitmapUtils() {
    }

    public static Bitmap bitmapFromUri(@NonNull ContentResolver resolver, @NonNull Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(resolver, uri);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            return null;
        }
    }
}
