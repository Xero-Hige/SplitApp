package ar.uba.fi.splitapp;

import android.app.Activity;
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

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class AttendesActivity extends AppCompatActivity {

    public static final String COMING_FROM_TASK = "task";
    LinkedList<String> lista = new LinkedList<>();
    String id_event = "error";
    private boolean coming_from_task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        Intent intent = getIntent();
        coming_from_task = intent.getBooleanExtra(COMING_FROM_TASK, false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chooser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout friends = (LinearLayout) findViewById(R.id.friends_container);
        LayoutInflater inflater = getLayoutInflater();
        if (!lista.isEmpty()) {
            return;
        }

        Bundle id = getIntent().getExtras();
        if (id != null) {
            id_event = id.getString("id");
        }
        SplitAppLogger.writeLog(1, "ID ES: " + id_event);
        ServerHandler.executeGet(id_event, ServerHandler.EVENT_LIST, Profile.getCurrentProfile().getId(), "", result -> {

            //onSucces.execute(result);
            if (result == null) {
                SplitAppLogger.writeLog(1, "ERROR EN LECUTRA");
                //onError.execute(null);
            } else try {

                JSONObject event = result.getJSONObject("data");
                JSONArray attendees = event.getJSONArray("invitees");

                for (int i = 0; i < attendees.length(); i++) {
                    String fb_id = attendees.getJSONObject(i).getString("facebook_id");
//                    SplitAppLogger.writeLog(1,"Mi FB id es: " + fb_id);
                    lista.push(fb_id);
                }

                SplitAppLogger.writeLog(1, "Tengo cantidad: " + lista.size());
            } catch (JSONException e) {
                //onError.execute(null);
                e.printStackTrace();
                SplitAppLogger.writeLog(1, "Exploto el JSON");
                return;
            }


            FacebookManager.executeWithFriendlist(Profile.getCurrentProfile().getId(), (names, ids) ->
                {
                        addFriendLayout(friends, inflater, Profile.getCurrentProfile().getName(), Profile.getCurrentProfile().getId());
                        for (int i = 0; i < names.size(); i++) {
                            String text = names.get(i);
                            String userId = ids.get(i);

                            addFriendLayout(friends, inflater, text, userId);
                        }
                }
            );
        });
    }

    private void addFriendLayout(LinearLayout friends, LayoutInflater inflater, String text, String userId) {

        View friendLayout = inflater.inflate(R.layout.friend_see_layout, null);

        TextView name = (TextView) friendLayout.findViewById(R.id.friend_name);

        name.setText(text);


        CircularImageView image = (CircularImageView) friendLayout.findViewById(R.id.friend_chooser_pic);
        FacebookManager.fillWithUserPic(userId, image, getApplicationContext());

        ImageView background = (ImageView) friendLayout.findViewById(R.id.friend_background);
        FacebookManager.fillWithUserCover(userId, background, getApplicationContext());

        friendLayout.setOnClickListener(v -> {
            if (coming_from_task) {

                Intent resultIntent = new Intent();
                resultIntent.putExtra(NewTaskDialogFragment.FACEBOOK_ID, userId);
                resultIntent.putExtra(NewTaskDialogFragment.NAME_FRIEND , text);
                setResult(Activity.RESULT_OK, resultIntent);
                NewTaskDialogFragment.facebook_id = userId;
                NewTaskDialogFragment.name_friend = text;;
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

            } else {
                Utility.showMessage(text, Utility.getViewgroup(this));
                Intent chat = new Intent(this, ChatSessionActivity.class);
                chat.putExtra(ChatSessionActivity.EXTRA_FRIEND_ID, userId);
                chat.putExtra(ChatSessionActivity.EXTRA_FRIEND_NAME, text);
                startActivity(chat);
            }
            //finish();
        });

        SplitAppLogger.writeLog(1, "En la lista hay " + lista.size());
        if (lista.contains(userId)) {
            friends.addView(friendLayout);
            SplitAppLogger.writeLog(1, "Contengo pero no agregue");
        }
    }

}