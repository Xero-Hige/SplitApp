package ar.uba.fi.splitapp;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AttendesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chooser);

        LinearLayout friends = (LinearLayout) findViewById(R.id.friends_container);
        LayoutInflater inflater = getLayoutInflater();

        List<String> lista = new ArrayList<>();

        ServerHandler.executeGet("2", ServerHandler.EVENT_DETAIL, "", "", result -> {
            //onSucces.execute(result);
            if (result == null) {
                //onError.execute(null);
            } else try {
                JSONObject events = result.getJSONObject("data");

                JSONArray attendees = events.getJSONArray("invitees");

                for (int i = 0; i < attendees.length(); i++) {
                    String fb_id = attendees.getJSONObject(i).getString("facebook_id");
                    lista.add(fb_id);
                }

            } catch (JSONException e) {
                //onError.execute(null);
                return;
            }
        });

        FacebookManager.executeWithFriendlist(Profile.getCurrentProfile().getId(), (names, ids) ->
                {
                    for (int i = 0; i < names.size(); i++) {
                        View friendLayout = inflater.inflate(R.layout.friend_see_layout, null);

                        TextView name = (TextView) friendLayout.findViewById(R.id.friend_name);
                        name.setText(names.get(i));

                        CircularImageView image = (CircularImageView) friendLayout.findViewById(R.id.friend_chooser_pic);
                        FacebookManager.fillWithUserPic(ids.get(i), image, getApplicationContext());

                        ImageView background = (ImageView) friendLayout.findViewById(R.id.friend_background);
                        FacebookManager.fillWithUserCover(ids.get(i), background, getApplicationContext());

                        final int finalI = i;
                        friendLayout.setOnClickListener(v -> {
                            Utility.showMessage(names.get(finalI), Utility.getViewgroup(this));
                            Intent chat = new Intent(this, ChatSessionActivity.class);
                            chat.putExtra(ChatSessionActivity.EXTRA_FRIEND_ID, ids.get(finalI));
                            chat.putExtra(ChatSessionActivity.EXTRA_FRIEND_NAME, names.get(finalI));
                            startActivity(chat);
                            //finish();
                        });

                        if (lista.contains(Profile.getCurrentProfile().getId())) {
                            friends.addView(friendLayout);
                        } else if (lista.contains(ids.get(i))){
                            friends.addView(friendLayout);
                        }

                    }
                }
        );
    }
}