package ar.uba.fi.splitapp;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class SendInviteConfirmationFragment extends DialogFragment {

    ConfirmationDialogListener mListener;
    private String inviteeName = "";
    private String facebookId = "";

    String getInviteeName() {
        return inviteeName;
    }

    String getFacebookId() {
        return facebookId;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Bundle args = getArguments();
        inviteeName = args.getString("name");
        facebookId = args.getString("facebookId");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Invitar a " + inviteeName + "?")
                .setPositiveButton("CONFIRMAR", (dialog, id) -> {
                    mListener.onDialogConfirm(SendInviteConfirmationFragment.this);
                })
                .setNegativeButton("CANCELAR", (dialog, id) -> {
                    mListener.onDialogCancel(SendInviteConfirmationFragment.this);
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConfirmationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ConfirmationDialogListener {
        void onDialogConfirm(SendInviteConfirmationFragment dialog);

        void onDialogCancel(SendInviteConfirmationFragment dialog);
    }

}
