package ar.uba.fi.splitapp;

import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.pkmmte.view.CircularImageView;

public class FriendChooserActivity extends AppCompatActivity
        implements SendInviteConfirmationFragment.ConfirmationDialogListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chooser);

        LinearLayout friends = (LinearLayout) findViewById(R.id.friends_container);
        LayoutInflater inflater = getLayoutInflater();

        FacebookManager.executeWithFriendlist(Profile.getCurrentProfile().getId(), (names, ids) ->
            {
                for (int i = 0; i < names.size(); i++) {
                    View friendLayout = inflater.inflate(R.layout.friend_choose_layout, null);

                    TextView name = (TextView) friendLayout.findViewById(R.id.friend_name);
                    name.setText(names.get(i));

                    CircularImageView image = (CircularImageView) friendLayout.findViewById(R.id.friend_chooser_pic);
                    FacebookManager.fillWithUserPic(ids.get(i), image, getApplicationContext());

                    ImageView background = (ImageView) friendLayout.findViewById(R.id.friend_background);
                    FacebookManager.fillWithUserCover(ids.get(i), background, getApplicationContext());

                    final int finalI = i;
                    friendLayout.setOnClickListener(v -> {
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
    public void onDialogConfirm(SendInviteConfirmationFragment dialog) {
        SplitAppLogger.writeLog(1, "Invitar a " + dialog.getInviteeName() + " " + dialog.getFacebookId());
        // TODO: Marcar que ese usuario fue invitado y luego hacer el POST al server cuando se crea el evento
    }

    @Override
    public void onDialogCancel(SendInviteConfirmationFragment dialog) {
        SplitAppLogger.writeLog(1, "No invitar a " + dialog.getInviteeName() + " " + dialog.getFacebookId());
    }
}
