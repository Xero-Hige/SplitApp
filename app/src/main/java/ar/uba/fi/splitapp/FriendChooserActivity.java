package ar.uba.fi.splitapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.pkmmte.view.CircularImageView;

public class FriendChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                            Utility.showMessage("Agregaste a: " + names.get(finalI), Utility.getViewgroup(this));
                            //finish();
                        });

                        friends.addView(friendLayout);
                    }
                }
        );
    }
}
