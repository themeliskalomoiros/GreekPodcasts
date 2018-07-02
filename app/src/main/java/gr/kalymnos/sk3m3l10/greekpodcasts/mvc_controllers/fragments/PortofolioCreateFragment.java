package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.BitmapUtils;

public class PortofolioCreateFragment extends Fragment implements PortofolioCreateViewMvc.OnPosterClickListener {

    private PortofolioCreateViewMvc viewMvc;
    private static final int RC_POSTER_PIC = 1331;

    //  Cache uri instead of Bitmap because the latter is too large (could be more than 5Mb) and
    //  throws an exception!
    private Uri cachedPosterUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMvc = new PortofolioCreateViewMvcImpl(inflater, container);
        viewMvc.setOnPosterClickListener(this);
        return viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Podcast.POSTER_KEY)) {
            cachedPosterUri = savedInstanceState.getParcelable(Podcast.POSTER_KEY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cachedPosterUri != null) {
            Bitmap poster = BitmapUtils.bitmapFromUri(getContext().getContentResolver(),
                    cachedPosterUri);
            viewMvc.bindPoster(poster);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_POSTER_PIC) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    //  Get the URI of the selected file
                    cachedPosterUri = data.getData();
                    viewMvc.bindPoster(BitmapUtils.bitmapFromUri(getContext().getContentResolver(), cachedPosterUri));
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(Podcast.POSTER_KEY, cachedPosterUri);
    }

    @Override
    public void onPosterClick() {
        //  Open gallery so user can pick an image.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, RC_POSTER_PIC);
        }
    }
}
