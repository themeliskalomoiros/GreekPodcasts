package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class InsertTextDialogFragment extends DialogFragment {

    public static final String TITLE_KEY = "title key";
    public static final String TAG = InsertTextDialogFragment.class.getSimpleName();

    interface OnInsertedTextListener {
        void onTextInserted(String text);
    }

    private OnInsertedTextListener callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (doesTitleExists()) {
            return createAlertDialog();
        } else {
            throw new UnsupportedOperationException(TAG + ": title is missing");
        }
    }

    private boolean doesTitleExists() {
        return !TextUtils.isEmpty(getContext().getString(getTitleRes()));
    }

    private Dialog createAlertDialog() {
        AlertDialog.Builder
                builder = new AlertDialog.Builder(getActivity(), R.style.InsertTextDialog);

        EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setMessage(getTitleRes())
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    if (callback != null)
                        callback.onTextInserted(input.getText().toString());
                })
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    private int getTitleRes() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(TITLE_KEY)) {
            return args.getInt(TITLE_KEY);
        } else {
            throw new UnsupportedOperationException(TAG + ": args are null or a title for the dialog is missing!");
        }
    }

    public void setOnInsertedTextListener(OnInsertedTextListener callback) {
        this.callback = callback;
    }
}
