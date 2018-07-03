package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model;

import android.app.Activity;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public class FakeDataRepo implements DataRepository {

    private static final String TAG = FakeDataRepo.class.getSimpleName();
    private static final long SLEEP_TIME = 500;

    private static final String TITLE_LABEL = "This is a random title";
    private static final String DESCRIPTION = "This is random a description";

    private static final String PUSH_ID = "just a push id";

    private static final String IMG_URL = "https://images-na.ssl-images-amazon.com/images/I/51-hDsBas0L.jpg";
    private static final String IMG_URL_2 = "http://cdn2.wpbeginner.com/wp-content/uploads/2017/02/category-description.jpg";
    private static final String IMG_URL_3 = "https://images.pexels.com/photos/219998/pexels-photo-219998.jpeg?auto=compress&cs=tinysrgb&h=350";
    private static final String IMG_URL_4 = "https://images.pexels.com/photos/33044/sunflower-sun-summer-yellow.jpg?auto=compress&cs=tinysrgb&h=350";
    private static final String IMG_URL_5 = "data:image/jpeg;base64,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";
    private static final String IMG_URL_6 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXjdyaOkSl7qO07NTwHYJbzIuY7Mu9JFxISyyAnSYfsGwskL8K";
    private static final String IMG_URL_7 = "https://i2-prod.mirror.co.uk/incoming/article11840943.ece/ALTERNATES/s615/PAY-MATING-BUGS.jpg";
    private static final String IMG_URL_8 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMDNeRoMvOlbbPAmMxbdOa5ODFtkhCEZtQquA__k63RvET-HPfmg";
    private static final String[] IMAGE_URLS = {IMG_URL, IMG_URL_2, IMG_URL_3, IMG_URL_4, IMG_URL_5, IMG_URL_6, IMG_URL_7, IMG_URL_8};

    private static final String EPISODE_URL_1 = "https://www.mfiles.co.uk/mp3-downloads/chopin-nocturne-op9-no2.mp3";
    private static final String EPISODE_URL_2 = "https://www.mfiles.co.uk/mp3-downloads/francisco-tarrega-lagrima.mp3";
    private static final String EPISODE_URL_3 = "https://www.mfiles.co.uk/mp3-downloads/bach-bourree-in-e-minor-piano.mp3";
    private static final String[] EPISODE_URLS = {EPISODE_URL_1, EPISODE_URL_2, EPISODE_URL_3};

    //  Special integer that will be used to generate pseudo random ids
    private static int id = 0;


    private static final String CATEGORY_1 = "Education";
    private static final String CATEGORY_2 = "News & Politics";
    private static final String CATEGORY_3 = "Stories";
    private static final String CATEGORY_4 = "Arts & Entertainment";
    private static final String CATEGORY_5 = "Music";
    private static final String CATEGORY_6 = "Sports";

    @Override
    public List<Podcast> fetchAllPodcasts() {
        sleep(SLEEP_TIME);
        return generatePodcastObjects(new Random().nextInt(40) + 1);
    }

    @Override
    public List<Podcast> fetchPodcastsFromPodcaster(String podcasterPushId) {
        return fetchAllPodcasts();
    }

    @Override
    public List<Podcast> fetchStarredPodcasts(Cursor starredPodcastsCursor) {
        return fetchAllPodcasts();
    }

    @Override
    public List<Episode> fetchEpisodes(String episodesId) {
        sleep(SLEEP_TIME);
        return generateEpisodeObjects(new Random().nextInt(40) + 1);
    }

    @Override
    public List<Category> fetchAllCategories() {
        sleep(SLEEP_TIME);
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(CATEGORY_1, DESCRIPTION, IMG_URL_2));
        categories.add(new Category(CATEGORY_2, DESCRIPTION, IMG_URL_2));
        categories.add(new Category(CATEGORY_3, DESCRIPTION, IMG_URL_2));
        categories.add(new Category(CATEGORY_4, DESCRIPTION, IMG_URL_2));
        categories.add(new Category(CATEGORY_5, DESCRIPTION, IMG_URL_2));
        categories.add(new Category(CATEGORY_6, DESCRIPTION, IMG_URL_2));
        return categories;
    }

    @Override
    public List<PromotionLink> fetchPromotionLinks(String podcasterId) {
        sleep(SLEEP_TIME);
        List<PromotionLink> links = new ArrayList<>();
        links.add(new PromotionLink("Support me on Patreon", "https://www.patreon.com/powerplaychess", PUSH_ID, PUSH_ID));
        links.add(new PromotionLink("Friend me on Facebook", "https://www.facebook.com/madonna/", PUSH_ID, PUSH_ID));
        return links;
    }

    @Override
    public Podcaster fetchPodcaster(String pushId) {
        sleep(SLEEP_TIME);
        return new Podcaster("panos@gmail.com", "Panos Sketos", "This is my personal statement as Panos " +
                "Sketos and bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla "
                , IMG_URL, PUSH_ID, System.currentTimeMillis());
    }

    @Override
    public String fetchPodcasterName(String pushId) {
        return "Panos Sketos";
    }

    @Override
    public void createPodcaster(@NonNull Activity activity, @NonNull String pushId, Runnable actionAfterCreation) {
        sleep(SLEEP_TIME);
        activity.runOnUiThread(actionAfterCreation);
    }


    @Override
    public boolean podcasterExists(String pushId) {
        sleep(SLEEP_TIME);
        return true;
    }

    private static void sleep(long timeMilli) {
        try {
            Thread.sleep(timeMilli);
        } catch (InterruptedException e) {
            Log.e(TAG, "Problem at sleep(). " + e.getMessage());
        }
    }

    private static List<Podcast> generatePodcastObjects(int count) {
        resetId();
        List<Podcast> podcasts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            podcasts.add(generatePodcastObject());
        }
        return podcasts;
    }

    private static Podcast generatePodcastObject() {
        Random random = new Random();
        int podcastsCount = random.nextInt(30) + 5;
        int randomImageIndex = random.nextInt(IMAGE_URLS.length);
        Podcast obj = new Podcast(TITLE_LABEL, getIncresedStringId(), IMAGE_URLS[randomImageIndex], DESCRIPTION, getStringId(),
                PUSH_ID);
        obj.setFirebasePushId("firebase_push_id_" + getStringId());
        return obj;
    }

    private static List<Episode> generateEpisodeObjects(int count) {
        resetId();
        List<Episode> episodes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            episodes.add(generateEpisodeObject());
        }
        return episodes;
    }

    private static Episode generateEpisodeObject() {
        int minutes = new Random().nextInt(100) + 1;
        int seconds = new Random().nextInt(60) + 1;
        Episode obj = new Episode(TITLE_LABEL + "_" + id, randomEpisodeUrl(), minutes, seconds, System.currentTimeMillis());
        obj.setFirebasePushId("firebase_push_id_" + getIncresedStringId());
        return obj;
    }

    /*  I increase the id to create fake seperate ids for every object. Need it to be like this to
     * synchronize with the local database.*/
    private static String getIncresedStringId() {
        return "" + (++id);
    }

    private static String getStringId() {
        return "" + id;
    }

    private static void resetId() {
        id = 0;
    }

    private static String randomEpisodeUrl() {
        int randomChoice = new Random().nextInt(2);
        return EPISODE_URLS[randomChoice];
    }
}
