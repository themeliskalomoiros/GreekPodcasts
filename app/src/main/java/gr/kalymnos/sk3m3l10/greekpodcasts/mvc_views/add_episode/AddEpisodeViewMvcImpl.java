package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.add_episode;

import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class AddEpisodeViewMvcImpl implements AddEpisodeViewMvc {

    private View rootView;
    private FloatingActionButton uploadButton;
    private EditText titleEditText;
    private ImageView insertAudioImageView;

    public AddEpisodeViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.add_episode, parent, false);
        uploadButton = rootView.findViewById(R.id.upload_audio_fab);
        titleEditText = rootView.findViewById(R.id.insert_title_edit_text);
        insertAudioImageView = rootView.findViewById(R.id.insert_audio_imageview);
    }

    @Override
    public String getInsertedTitle() {
        return titleEditText.getText().toString();
    }

    @Override
    public void setOnActionsClickListener(OnActionsClickListener listener) {
        if (listener != null) {
            uploadButton.setOnClickListener(view -> listener.onUploadActionClick());
            insertAudioImageView.setOnClickListener(view -> listener.onInsertAudioClick());
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
