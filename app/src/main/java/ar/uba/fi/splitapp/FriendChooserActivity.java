package ar.uba.fi.splitapp;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FriendChooserActivity extends AppCompatActivity
        implements SendInviteConfirmationFragment.ConfirmationDialogListener{

    public static final String INVITEES = "invitees";
    public static final String ALREADY_INVITED = "attendees";
    public static final String FROM_CURRENT_EVENT = "current_event";

    ArrayList<String> inviteesID = new ArrayList<>();
    Map<String, View> idView = new HashMap<>();
    boolean from_current_event;
    ArrayList<String> attendeesID = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chooser);

        LinearLayout friends = (LinearLayout) findViewById(R.id.friends_container);
        LayoutInflater inflater = getLayoutInflater();

        Intent intent = getIntent();
        from_current_event = intent.getBooleanExtra(FROM_CURRENT_EVENT,false);

        if (from_current_event) {
            attendeesID = intent.getStringArrayListExtra(ALREADY_INVITED);
        }

        FacebookManager.executeWithFriendlist(Profile.getCurrentProfile().getId(), (names, ids) ->
            {
                for (int i = 0; i < names.size(); i++) {
                    View friendLayout = inflater.inflate(R.layout.friend_choose_layout, null);
                    idView.put(ids.get(i), friendLayout);
                    if (from_current_event) {
                        if (attendeesID.contains(ids.get(i))) {
                            friendLayout.setAlpha(0.5f);
                        }
                    }

                    TextView name = (TextView) friendLayout.findViewById(R.id.friend_name);
                    name.setText(names.get(i));

                    CircularImageView image = (CircularImageView) friendLayout.findViewById(R.id.friend_chooser_pic);
                    FacebookManager.fillWithUserPic(ids.get(i), image, getApplicationContext());

                    ImageView background = (ImageView) friendLayout.findViewById(R.id.friend_background);
                    FacebookManager.fillWithUserCover(ids.get(i), background, getApplicationContext());

                    final int finalI = i;
                    friendLayout.setOnClickListener(v -> {
                        if (attendeesID.contains(ids.get(finalI)) || inviteesID.contains(ids.get(finalI))) // No puedo seleccionar dos veces al mismo
                            return;
                        DialogFragment newFragment = new SendInviteConfirmationFragment();
                        Bundle args = new Bundle();
                        args.putString("name", names.get(finalI));
                        args.putString("facebookId", ids.get(finalI));
                        newFragment.setArguments(args);
                        newFragment.show(getSupportFragmentManager(), "sarasa");
                        //finish();
                    });

                    friends.addView(friendLayout);
                }
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend_chooser_taskbar, menu);
        return true;
    }

    @Override
    public void onDialogConfirm(SendInviteConfirmationFragment dialog) {
        SplitAppLogger.writeLog(1, "Invitar a " + dialog.getInviteeName() + " " + dialog.getFacebookId());
        final String fbId = dialog.getFacebookId();
        inviteesID.add(fbId);
        idView.get(fbId).setAlpha(0.5f);
        // TODO: Marcar que ese usuario fue invitado y luego hacer el POST al server cuando se crea el evento
    }

    @Override
    public void onDialogCancel(SendInviteConfirmationFragment dialog) {
        SplitAppLogger.writeLog(1, "No invitar a " + dialog.getInviteeName() + " " + dialog.getFacebookId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra(INVITEES, inviteesID);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
