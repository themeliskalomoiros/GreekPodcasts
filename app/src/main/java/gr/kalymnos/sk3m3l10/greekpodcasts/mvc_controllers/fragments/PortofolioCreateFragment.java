package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.create.PortofolioCreateViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.create.PortofolioCreateViewMvcImpl;

public class PortofolioCreateFragment extends Fragment implements PortofolioCreateViewMvc.OnPosterClickListener {

    private PortofolioCreateViewMvc viewMvc;
    private static final int RC_POSTER_PIC = 1331;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMvc = new PortofolioCreateViewMvcImpl(inflater, container);
        viewMvc.setOnPosterClickListener(this);
        return viewMvc.getRootView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_POSTER_PIC) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    //  Get the URI of the selected file
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),uri);
                        viewMvc.bindPoster(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onPosterClick() {
        //  Open gallery so user can pick an image.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, RC_POSTER_PIC);
        }
    }
}
