package com.xfsi.swipe_demo1.contents;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.text.TextUtils;

import com.xfsi.swipe_demo1.R;

/**
 * Created by local-kieu on 3/28/16.
 */
public class ConfirmationDialogFragment extends DialogFragment {
    public static final String ARG_RESOURCES = "resources";

    public static ConfirmationDialogFragment newInstance(String[] resources) {
        ConfirmationDialogFragment fg = new ConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_RESOURCES, resources);
        fg.setArguments(args);
        return fg;
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] resources = savedInstanceState.getStringArray(ARG_RESOURCES);
        return new AlertDialog.Builder(getActivity())
                .setMessage( getString(R.string.confirmation, TextUtils.join("\n", resources)))
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface inf, int which) {
                       // ((Listener)getParentFragment()).doPositiveClick();
                    }
                })
                .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface inf, int Which) {
                       // ((Listener)getParentFragment()).doNegativeClick();
                    }
                })
                .create();
    }

    public interface Listener {
        public void onConfirmation(boolean allowed);
    }

}
