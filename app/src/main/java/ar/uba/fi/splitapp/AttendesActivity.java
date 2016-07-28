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

    private boolean coming_from_task;

    LinkedList<String> lista = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        Intent intent = getIntent();
        coming_from_task = intent.getBooleanExtra(COMING_FROM_TASK,false);

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

        ServerHandler.executeGet(ServerHandler.EVENT_LIST, "", "", result -> {
            SplitAppLogger.writeLog(1, "Agus Respondio");
            //onSucces.execute(result);
            if (result == null) {
                SplitAppLogger.writeLog(1, "ERROR EN LECUTRA");
                //onError.execute(null);
            } else try {
                JSONArray events = result.getJSONArray("data");
                JSONObject event1 = events.getJSONObject(0);
                JSONArray attendees = event1.getJSONArray("invitees");

                SplitAppLogger.writeLog(1, "Tengo cantidad: " + attendees.length());

                for (int i = 0; i < attendees.length(); i++) {
                    String fb_id = attendees.getJSONObject(i).getString("facebook_id");
//                    SplitAppLogger.writeLog(1,"Mi FB id es: " + fb_id);
                    lista.push(fb_id);
                }
            } catch (JSONException e) {
                //onError.execute(null);
                e.printStackTrace();
                SplitAppLogger.writeLog(1, "Exploto el JSON");
                return;
            }


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
                            if (coming_from_task) {

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(NewTaskDialogFragment.FACEBOOK_ID, ids.get(finalI));
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();

                            } else {
                                Utility.showMessage(names.get(finalI), Utility.getViewgroup(this));
                                Intent chat = new Intent(this, ChatSessionActivity.class);
                                chat.putExtra(ChatSessionActivity.EXTRA_FRIEND_ID, ids.get(finalI));
                                chat.putExtra(ChatSessionActivity.EXTRA_FRIEND_NAME, names.get(finalI));
                                startActivity(chat);
                            }
                            //finish();
                        });

//                        SplitAppLogger.writeLog(1,"A Comparar 1: " + Profile.getCurrentProfile().getId());
//                        SplitAppLogger.writeLog(1,"A Comparar 2: " + ids.get(i));

                        SplitAppLogger.writeLog(1, "Cantidad de elementos: " + lista.size());

//                            for (int j = 0; j < lista.size(); j++) {
//                                SplitAppLogger.writeLog(1, "Elemento de la lista comparando: " + lista.get(j));
//                                if (lista.get(j) == Profile.getCurrentProfile().getId()) {
//                                    friends.addView(friendLayout);
//                                    SplitAppLogger.writeLog(1, "Contengo pero no agregue");
//                                } else if (lista.get(j) == ids.get(i)) {
//                                    friends.addView(friendLayout);
//                                    SplitAppLogger.writeLog(1, "Contengo pero no agregue 2");
//                                }
//                            }

                        SplitAppLogger.writeLog(1, "En la lista hay " + lista.size());
                        if (lista.contains(Profile.getCurrentProfile().getId())
                                || lista.contains(ids.get(i))) {
                            friends.addView(friendLayout);
                            SplitAppLogger.writeLog(1, "Contengo pero no agregue");
                        }
                    }
                }
            );
        });
    }

}