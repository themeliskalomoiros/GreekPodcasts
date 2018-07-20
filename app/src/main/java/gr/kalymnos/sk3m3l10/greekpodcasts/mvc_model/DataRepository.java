package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

/*  The web service that hosts data about podcasts, podcasters etc...   */

public interface DataRepository {

    interface OnCreatedPodcastListener {
        void onPodcastCreationSuccess(String podcastPushId);

        void onPodcastCreationFailure(String message);

    }

    interface OnAudioUploadSuccessListener {
        void onSuccess();
    }

    interface OnAllPodcastsFetchedSuccessListener {
        void onAllPodcastsFetchedSuccess(List<Podcast> podcasts);
    }

    void setOnAllPodcastsFetchedSuccessListener(OnAllPodcastsFetchedSuccessListener listener);

    List<Podcast> fetchAllPodcasts();

    List<Podcast> fetchPodcastsFromPodcaster(String podcasterPushId);

    List<Podcast> fetchPodcastsFromCategory(String categoryPushId);

    List<Podcast> fetchStarredPodcasts(Cursor starredPodcastsCursor);

    List<Episode> fetchEpisodes(String episodesId);

    List<Category> fetchAllCategories();

    List<PromotionLink> fetchPromotionLinks(String podcasterId);

    Podcaster fetchPodcaster(String pushId);

    String fetchPodcasterName(String pushId);

    void createPodcaster(@NonNull Activity activity, @NonNull String pushId, Runnable actionAfterCreation);

    boolean podcasterExists(String pushId);

    String getCurrentUserUid();

    void createNewPodcast(Podcast podcast);

    void setOnCreatedPodcastListener(OnCreatedPodcastListener listener);

    void uploadImage(String podcastPushId, byte[] posterData);

    void uploadAudio(Uri audioUri, String podcastPushId);

    void setOnAudioUploadSuccessListener(OnAudioUploadSuccessListener listener);
}
