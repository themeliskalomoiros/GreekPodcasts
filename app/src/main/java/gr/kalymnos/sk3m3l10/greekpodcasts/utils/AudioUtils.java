package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.content.Context;
import android.media.AudioManager;

public class AudioUtils {

    private AudioUtils() {
    }

    public static int getAudioFocusResult(Context context, AudioManager.OnAudioFocusChangeListener listener) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus for playback, this registers the afChangeListener
        return am.requestAudioFocus(listener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
    }

    public static void abandonAudioFocus(Context context, AudioManager.OnAudioFocusChangeListener listener) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(listener);
    }
}
