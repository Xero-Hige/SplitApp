package ar.uba.fi.splitapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.pkmmte.view.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Margonari on 27/07/2016.
 */
public class NewTaskDialogFragment extends DialogFragment {

    public int event_id;
    public static final String FACEBOOK_ID = "facebook_id";

    private String facebook_id;
    private CircularImageView image;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    facebook_id = data.getStringExtra(FACEBOOK_ID);
                    FacebookManager.fillWithUserPic(facebook_id, image, getContext());
                }
                break;
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View promptView = inflater.inflate(R.layout.dialog_new_task, null);



        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Nueva Tarea");
        ImageView imagenAmigos = (ImageView)promptView.findViewById(R.id.image_button_asignee);
        image = (CircularImageView) promptView.findViewById(R.id.asignee_pic);
        imagenAmigos.setOnClickListener(v -> {
            Intent friendListIntent = new Intent(getActivity(), AttendesActivity.class);
            friendListIntent.putExtra(AttendesActivity.COMING_FROM_TASK, true);
            String event_string = String.valueOf(event_id);
            friendListIntent.putExtra("id", event_string);
            startActivityForResult(friendListIntent, 0);
        });

        builder.setView(promptView)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // post task
                        System.out.println("FACEBOOK ID:" + facebook_id);

                        JSONObject obj = new JSONObject();
                        String nameTask = ((EditText)((Dialog)dialog).findViewById(R.id.name_new_task)).getText().toString();

                        try {
                            obj.put("assignee", facebook_id);
                            obj.put("name", nameTask);
                            obj.put("cost", 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(obj);


                        ServerHandler.executePost(String.valueOf(event_id),ServerHandler.EVENT_TASKS, Profile.getCurrentProfile().getId(), "", obj, result -> {
                            //onSucces.execute(result);
                            if (result == null) {
                                //onError.execute(null);
                            } else try {
                                JSONObject callback = result.getJSONObject("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel task
                    }
                });
        return builder.create();
    }

}
