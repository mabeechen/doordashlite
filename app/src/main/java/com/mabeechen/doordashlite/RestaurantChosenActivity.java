package com.mabeechen.doordashlite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * A basic dialog to display messages
 *
 * @author mabeechen
 * @since 8/3/17
 */
public class RestaurantChosenActivity extends AppCompatActivity {

    private static final String DIALOG_TAG = "simpleDialogTag";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getFragmentManager().findFragmentByTag(DIALOG_TAG) == null) {
            (new SimpleDialogFragment()).show(getFragmentManager(), DIALOG_TAG);
        }
    }

    /**
     * Get parameters
     * @return
     */
    protected Bundle getParameters() {
        return getIntent().getExtras();
    }

    /**
     * Simple dialog fragment
     * @author mabeechen
     * @since 8/3/17
     */
    public static class SimpleDialogFragment extends DialogFragment {

        /*
         * (non-Javadoc)
         *
         * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            RestaurantChosenActivity activity = (RestaurantChosenActivity)getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Title");
            builder.setMessage("Message");

            String positiveButtonText = "Yes";
            if (positiveButtonText == null) {
                positiveButtonText = activity.getString(android.R.string.ok);
            }

            builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RestaurantChosenActivity activity = (RestaurantChosenActivity)getActivity();
//                    TODO: launch intent

                    activity.finish();
                }
            });

            String cancelButtonTitle = "No";
            if (cancelButtonTitle != null) {
                builder.setNegativeButton(cancelButtonTitle, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
            }

            return builder.create();
        }
    }
}
