package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.add_episode;

import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class AddEpisodeViewMvcImpl implements AddEpisodeViewMvc {

    private View rootView;
    private FloatingActionButton uploadButton;
    private EditText titleEditText;
    private ImageView insertAudioImageView;
    private TextView chosenFileName, audioHint;

    public AddEpisodeViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.add_episode, parent, false);
        uploadButton = rootView.findViewById(R.id.upload_audio_fab);
        titleEditText = rootView.findViewById(R.id.insert_title_edit_text);
        insertAudioImageView = rootView.findViewById(R.id.insert_audio_imageview);
        chosenFileName = rootView.findViewById(R.id.chosen_audio_file_name);
        audioHint = rootView.findViewById(R.id.click_to_insert_audio_label);
    }

    @Override
    public int getEnterTitleAndSelectAudioMessage() {
        return R.string.enter_title_select_audio_message;
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
    public void bindFileName(String filename) {
        chosenFileName.setText(filename);
    }

    @Override
    public void displayAudioHint(boolean display) {
        if (display) {
            audioHint.setVisibility(View.VISIBLE);
        } else {
            audioHint.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void displayFileName(boolean display) {
        if (display) {
            chosenFileName.setVisibility(View.VISIBLE);
        } else {
            chosenFileName.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void drawHeadsetMic(boolean draw) {
        if (draw) {
            insertAudioImageView.setImageResource(R.drawable.ic_headset_mic_white_140dp);
        } else {
            insertAudioImageView.setImageResource(R.drawable.ic_headset_mic_light_black_140dp);
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
