package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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

    public static Bitmap bitmapFromUrl(@NonNull URL imageUrl) {
        try {
            URLConnection connection = imageUrl.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            return BitmapFactory.decodeStream(bufferedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getImageDataFromImageView(ImageView imageView) {
        //  Get the bitmap
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
