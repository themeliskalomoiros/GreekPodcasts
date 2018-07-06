package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

public interface ChangeSaver {
    
    static final int MIN_TITLE_LENGTH = 3;
    static final int MAX_TITLE_LENGTH = 80;
    static final int MIN_DESCRIPTION_LENGTH = 20;
    static final int MAX_DESCRIPTION_LENGTH = 300;

    void save();

    String getConfirmationMessage();
}
