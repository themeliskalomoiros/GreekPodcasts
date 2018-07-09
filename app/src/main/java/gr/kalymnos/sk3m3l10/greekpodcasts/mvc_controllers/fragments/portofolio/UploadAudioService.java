package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;

public class UploadAudioService extends IntentService implements DataRepository.OnAudioUploadSuccessListener{

    private static final String ACTION_UPLOAD_AUDIO = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.action.upload_audio";
    private static final String EXTRA_AUDIO_URI = "gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio.extra.audio_uri";

    public UploadAudioService() {
        super("UploadAudioService");
    }

    public static void startActionUploadAudio(Context context, Uri audioUri) {
        Intent intent = new Intent(context, UploadAudioService.class);
        intent.setAction(ACTION_UPLOAD_AUDIO);
        intent.putExtra(EXTRA_AUDIO_URI, audioUri);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_AUDIO.equals(action)) {
                final Uri audioUri = intent.getParcelableExtra(EXTRA_AUDIO_URI);
                handleActionUploadAudio(audioUri);
            }
        }
    }

    private void handleActionUploadAudio(Uri audioUri) {
        //  TODO: Replace with a real service
        DataRepository repo = new StaticFakeDataRepo();
        repo.setOnAudioUploadSuccessListener(this);
        repo.uploadAudio(audioUri);
    }

    @Override
    public void onSuccess() {
        //  TODO: Do something when audio upload is complete.
    }
}
