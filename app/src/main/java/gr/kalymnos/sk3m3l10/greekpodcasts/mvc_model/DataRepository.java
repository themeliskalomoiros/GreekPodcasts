package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model;

import android.database.Cursor;

import java.util.List;
import java.util.Locale;

import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

/*  The web service that hosts data about podcasts, podcasters etc...   */

public interface DataRepository {

    List<Podcast> fetchAllPodcasts();

    List<Podcast> fetchPodcastsFromPodcaster(String podcasterPushId);

    List<Podcast> fetchStarredPodcasts(Cursor starredPodcastsCursor);

    List<Episode> fetchEpisodes(String episodesId);

    List<Category> fetchAllCategories();

    List<PromotionLink> fetchPromotionLinks(String podcasterId);

    Podcaster fetchPodcaster(String pushId);

    String fetchPodcasterName(String pushId);

    void createPodcaster(String pushId);

    boolean podcasterExists(String pushId);
}
