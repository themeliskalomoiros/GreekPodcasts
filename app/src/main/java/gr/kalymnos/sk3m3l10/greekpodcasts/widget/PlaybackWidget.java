package gr.kalymnos.sk3m3l10.greekpodcasts.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.playback_service.PlaybackService;

/**
 * Implementation of App Widget functionality.
 */
public class PlaybackWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int playbackState, String episodeTitle, Bitmap poster) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.playback_widget);
        setUpWidgetUi(playbackState, episodeTitle, poster, views);
        setTransportControlOnClickListeners(context, views);


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setUpWidgetUi(int playbackState, String episodeTitle, Bitmap poster, RemoteViews views) {
        if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
            views.setImageViewResource(R.id.player_play_pause, R.drawable.ic_pause_white_40dp);
        } else {
            views.setImageViewResource(R.id.player_play_pause, R.drawable.ic_play_arrow_white_40dp);
        }

        if (!TextUtils.isEmpty(episodeTitle)) {
            views.setTextViewText(R.id.title_textview, episodeTitle);
        }

        if (poster != null) {
            views.setImageViewBitmap(R.id.poster_imageview, poster);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //  Start the PlaybackService class, it will take care of updating the widgets
        PlaybackService.startServiceToUpdateWidgets(context);
    }

    public static void updateAllWidgets(Context context, AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds, int playbackState, String episodeTitle, Bitmap poster) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, playbackState, episodeTitle, poster);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void setTransportControlOnClickListeners(Context context, RemoteViews views) {
        PendingIntent playPendingIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE);
        views.setOnClickPendingIntent(R.id.player_play_pause, playPendingIntent);

        PendingIntent previousPendingIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        views.setOnClickPendingIntent(R.id.player_previous, previousPendingIntent);

        PendingIntent nextPendingIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        views.setOnClickPendingIntent(R.id.player_next, nextPendingIntent);
    }
}

