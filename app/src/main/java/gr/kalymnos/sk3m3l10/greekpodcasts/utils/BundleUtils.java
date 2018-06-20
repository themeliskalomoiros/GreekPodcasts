package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;

import static gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode.*;
import static gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast.*;

public class BundleUtils {

    private BundleUtils() {
    }

    public static Bundle bundleForQuickPlayer(List<Episode> episodes, int episodeIndex,
                                              String podcastPosterUrl, String podcasterName, String podcasterPushId, String podcastPushId) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EPISODES_KEY, (ArrayList<? extends Parcelable>) episodes);
        bundle.putInt(INDEX_KEY, episodeIndex); //  the episode index
        bundle.putString(POSTER_KEY, podcastPosterUrl);
        bundle.putString(PODCASTER_KEY, podcasterName);
        bundle.putString(Podcaster.PUSH_ID_KEY, podcasterPushId);
        bundle.putString(Podcast.FIREBASE_ID_KEY, podcastPushId);
        return bundle;
    }

    public static Bundle bundleForEpisodeActivity(String podcastPosterUrl, String podcasterPushId) {
        Bundle bundle = new Bundle();
        bundle.putString(POSTER_KEY, podcastPosterUrl);
        bundle.putString(Podcaster.PUSH_ID_KEY, podcasterPushId);
        return bundle;
    }

    public static Bundle bundleForPodcasterActivity(String podcasterName, String podcasterPushId) {
        Bundle bundle = new Bundle();
        bundle.putString(Podcast.PODCASTER_KEY, podcasterName);
        bundle.putString(Podcaster.PUSH_ID_KEY, podcasterPushId);
        return bundle;
    }

    public static boolean containsKeys(Bundle bundle, String... keys) {
        for (String key : keys) {
            if (!bundle.containsKey(key)) {
                return false;
            }
        }
        return true;
    }
}
