package com.example.rakvat.iyana;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class HelpDialog extends DialogFragment {
    int mStringId;

    public static HelpDialog newInstance(int stringId) {
        HelpDialog f = new HelpDialog();
        Bundle args = new Bundle();
        args.putInt("stringId", stringId);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStringId = getArguments().getInt("stringId");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mStringId)
                .setPositiveButton(R.string.help_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}