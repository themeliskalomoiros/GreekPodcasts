package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class PromotionLinkDialogFragment extends DialogFragment {

    private EditText titleInput, urlInput;

    interface OnInsertedTextListener {
        void onInsertedText(String title, String url);
    }

    private OnInsertedTextListener callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog(initializeViews());
    }

    private Dialog createDialog(View root) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.InsertTextDialog);
        builder.setView(root)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (callback != null) {
                        callback.onInsertedText(titleInput.getText().toString(), urlInput.getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    @NonNull
    private View initializeViews() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.promotion_dialog, null);
        titleInput = root.findViewById(R.id.portofolio_title_edittext);
        urlInput = root.findViewById(R.id.portofolio_url_edittext);
        return root;
    }

    public void setOnInsertedTextListener(OnInsertedTextListener listener) {
        this.callback = listener;
    }
}
