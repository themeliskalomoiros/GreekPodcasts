package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.add_episode;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;

public interface AddEpisodeViewMvc extends ViewMvc {

    int getEnterTitleAndSelectAudioMessage();

    interface OnActionsClickListener {

        void onInsertAudioClick();

        void onUploadActionClick();
    }

    String getInsertedTitle();

    void setOnActionsClickListener(OnActionsClickListener listener);

    void bindFileName(String filename);

    void displayAudioHint(boolean display);

    void displayFileName(boolean display);

    void drawHeadsetMic(boolean draw);
}
