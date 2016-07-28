package ar.uba.fi.splitapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.pkmmte.view.CircularImageView;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventDescriptionActivity extends AppCompatActivity {

    String json_prop = "{\n" +
            "      \"name\": \"asd\",\n" +
            "      \"when\": \"2016-07-25 19:30:00\",\n" +
            "      \"when_iso\": \"2016-07-25T19:30:00-03:00\",\n" +
            "      \"lat\": -53.04,\n" +
            "      \"long\": -53.04,\n" +
            "      \"invitees\": [\n" +
            "        {\n" +
            "          \"facebook_id\": 231231231\n" +
            "        },\n" +
            "        {\n" +
            "          \"facebook_id\": 244\n" +
            "        }\n" +
            "      ],\n" +
            "      \"tasks\": [\n" +
            "        {\n" +
            "          \"assignee\": 231231231,\n" +
            "          \"name\": \"COMPRAR PAN\",\n" +
            "          \"cost\": 4.50,\n" +
            "          \"done\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"assignee\": 231231231,\n" +
            "          \"name\": \"COMPRAR PAN\",\n" +
            "          \"cost\": null,\n" +
            "          \"done\": false\n" +
            "        },\n" +
            "      ]\n" +
            "    }";


    private ExpandableLinearLayout mMyTasks;
    private ExpandableLinearLayout mAllTasks;
    private ExpandableLinearLayout mSettle;
    private Date when;
    private String id_event = "error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(eventMain, 0);
            }
        });


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        */

        Bundle event_id_passed = getIntent().getExtras();
        id_event = "error";
        if (event_id_passed != null) {
            id_event = event_id_passed.getString("id");
        }
        ServerHandler.executeGet(id_event, ServerHandler.EVENT_DETAIL, Profile.getCurrentProfile().getId(), "", result -> {
            //onSucces.execute(result);
            if (result == null) {
                //onError.execute(null);
            } else try {
                JSONObject events = result.getJSONObject("data");

                getSupportActionBar().setTitle(events.getString("name"));
                //toolbar.setTitle(events.getString("name"));

                String date_str = events.getString("when");
                Date date_class = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date_str);
                this.when = date_class;
                SplitAppLogger.writeLog(1, "WHEN seteado");
                String dateWithoutTime = new SimpleDateFormat("dd-MM-yyyy").format(date_class);
                String justTime = new SimpleDateFormat("HH:mm").format(date_class);


                TextView date = (TextView) findViewById(R.id.date_details);
                TextView time = (TextView) findViewById(R.id.time_details);
                TextView invitees = (TextView) findViewById(R.id.participants_details);

                date.setText(dateWithoutTime);
                time.setText(justTime);

                JSONArray attendees = events.getJSONArray("invitees");
                String cant_part = attendees.length() + " personas invitadas";
                invitees.setText(cant_part);

            } catch (JSONException e) {
                //onError.execute(null);
                return;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        addExpandables();

        addTaskStatus();

        viewFriends();

        LinearLayout templates = (LinearLayout) findViewById(R.id.my_tasks_list);
        LayoutInflater inflater = getLayoutInflater();

        setMyTasks(templates, inflater);

        templates = (LinearLayout) findViewById(R.id.settle_list);

        setSettle(templates, inflater, id_event);
    }

    private void viewFriends() {
        ImageView imagenAmigos = (ImageView) findViewById(R.id.imageButton4);

        imagenAmigos.setOnClickListener(v -> {
            Intent friendListIntent = new Intent(this, AttendesActivity.class);
            startActivityForResult(friendListIntent, 0);
        });
    }

    private void setMyTasks(LinearLayout templates, LayoutInflater inflater) {
        addAdTask(templates, inflater);
        for (int i = 1; i < 4; i++) {
            View templateItem = inflater.inflate(R.layout.my_task_status_layout, null);

            TextView text = (TextView) templateItem.findViewById(R.id.task_name);
            text.setText("Tarea #" + i);

            EditText price_in = (EditText) templateItem.findViewById(R.id.price_input);
            TextView price = (TextView) templateItem.findViewById(R.id.price);

            CheckBox done = (CheckBox) templateItem.findViewById(R.id.task_completed);

            price_in.setVisibility(View.VISIBLE);
            price.setVisibility(View.GONE);
            done.setChecked(false);

            done.setOnClickListener(v -> {
                if (!done.isChecked()) {
                    price_in.setText("");
                    price.setText("");
                    price_in.setVisibility(View.VISIBLE);
                    price.setVisibility(View.GONE);
                    done.setChecked(false);
                } else {
                    String settled_price = price_in.getText().toString();
                    price.setText("$" + (settled_price.equals("") ? "0" : settled_price));
                    price_in.setVisibility(View.GONE);
                    price.setVisibility(View.VISIBLE);
                    done.setChecked(true);
                    if (isFinalized()) {
                        done.setEnabled(false);
                        done.setOnClickListener(u -> {
                        });
                    }
                }
            });

            addPopUpMyTask(i, templateItem);

            CircularImageView profile = (CircularImageView) templateItem.findViewById(R.id.task_profile_pic);
            FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), profile, getApplicationContext());

            templates.addView(templateItem);
        }
    }

    private void addAdTask(LinearLayout templates, LayoutInflater inflater) {
        View templateItem = inflater.inflate(R.layout.ad_task_layout, null);
        templates.addView(templateItem);
    }

    public void adTaskClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.jumbo.com.ar/Comprar/Home.aspx?#_atCategory=false&_atGrilla=true&_query=coca"));
        startActivity(intent);
    }

    private void addPopUpMyTask(int i, View templateItem) {
        ImageView button = (ImageView) templateItem.findViewById(R.id.my_task_setting);

        DroppyMenuPopup.Builder dropdown = new DroppyMenuPopup.Builder(this, button);

        dropdown.addMenuItem(new DroppyMenuItem("Tarea #" + i).setClickable(false).setId(0)).addSeparator();
        dropdown.addMenuItem(new DroppyMenuItem("Creada el: 25 jul.").setClickable(false).setId(0));
        dropdown.addMenuItem(new DroppyMenuItem("Asignada a: Vos, duh").setClickable(false).setId(0));
        dropdown.addMenuItem(new DroppyMenuItem("Creada el: 25 jul.").setClickable(false).setId(0)).addSeparator();
        dropdown.addMenuItem(new DroppyMenuItem("Editar", R.drawable.pencil).setClickable(true).setId(1)).addSeparator();
        dropdown.addMenuItem(new DroppyMenuItem("VER SUGERENCIA DE COMPRA", R.drawable.location).setClickable(true).setId(2));

        dropdown.setOnClick((v, id) -> {
            switch (id) {
                case 0:
                    return;
                case 1:
                    Utility.showMessage("Editar", Utility.getViewgroup(EventDescriptionActivity.this));
                    return;
                case 2:
                    Utility.showMessage("Sugerencia", Utility.getViewgroup(EventDescriptionActivity.this));
                    return;
                default:
                    SplitAppLogger.writeLog(SplitAppLogger.ERRO, "The imposible has happend: Invalid menu ID");
            }
        });

        DroppyMenuPopup menu = dropdown.build();

        button.setOnClickListener(v -> menu.show());
    }

    private boolean isFinalized() {
        if (this.when == null) {
            SplitAppLogger.writeLog(1, "WHEN sin setear");
            return false;
        }
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(this.when);
        cal.add(Calendar.HOUR_OF_DAY, 12);
        Date finalizeDate = cal.getTime();
        return now.after(finalizeDate);
    }

    private void setSettle(LinearLayout templates, LayoutInflater inflater, String event_id) {
        View templateItem = inflater.inflate(R.layout.settlement_debt_layout, null);

        CircularImageView view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        ImageView background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(this.getApplicationContext()).load(R.drawable.debt_on).centerCrop().into(background);

        final ImageView finalBackground = background;
        templateItem.setOnClickListener(v -> {
            Intent makePayment = new Intent(this, PaymentListActivity.class);
            makePayment.putExtra("id", event_id);
            startActivity(makePayment);
            Glide.with(this.getApplicationContext()).load(R.drawable.debt_off).centerCrop().into(finalBackground);
        });
        templates.addView(templateItem);


        templateItem = inflater.inflate(R.layout.settlement_debt_layout, null);

        view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(this.getApplicationContext()).load(R.drawable.debt_off).centerCrop().into(background);

        templateItem.setOnClickListener(v -> {
            Intent makePayment = new Intent(this, PaymentListActivity.class);
            makePayment.putExtra("id", event_id);
            startActivity(makePayment);
        });
        templates.addView(templateItem);


        templateItem = inflater.inflate(R.layout.settlement_acred_layout, null);

        view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(this.getApplicationContext()).load(R.drawable.settle_on).centerCrop().into(background);

        final ImageView finalBackground1 = background;
        templateItem.setOnClickListener(v -> {
            Intent makePayment = new Intent(this, PaymentListActivity.class);
            makePayment.putExtra("id", event_id);
            startActivity(makePayment);
            Glide.with(this.getApplicationContext()).load(R.drawable.settle_off).centerCrop().into(finalBackground1);
        });
        templates.addView(templateItem);


        templateItem = inflater.inflate(R.layout.settlement_acred_layout, null);

        view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(this.getApplicationContext()).load(R.drawable.settle_off).centerCrop().into(background);

        templateItem.setOnClickListener(v -> {
            Intent makePayment = new Intent(this, PaymentListActivity.class);
            makePayment.putExtra("id", event_id);
            startActivity(makePayment);
        });
        templates.addView(templateItem);

    }


    private void addTaskStatus() {
        LinearLayout templates = (LinearLayout) findViewById(R.id.all_tasks_list);
        LayoutInflater inflater = getLayoutInflater();

        JSONArray cant_tareas;

        try {
            JSONObject objeto = new JSONObject(json_prop);
            cant_tareas = objeto.getJSONArray("tasks");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        int tareas = cant_tareas.length();

        for (int i = 1; i < tareas; i++) {
            View templateItem = inflater.inflate(R.layout.task_status_layout, null);

            TextView text = (TextView) templateItem.findViewById(R.id.task_name);
            String tarea;
            boolean hecho;
            try {
                tarea = cant_tareas.getJSONObject(i).getString("name");
                hecho = cant_tareas.getJSONObject(i).getBoolean("done");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }


            text.setText(tarea);

            TextView date = (TextView) templateItem.findViewById(R.id.task_status);
            if (hecho) {
                date.setText("Hecho");
            } else {
                date.setText("Pendiente");
            }


            CircularImageView profile = (CircularImageView) templateItem.findViewById(R.id.task_profile_pic);
            FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), profile, getApplicationContext());

            templates.addView(templateItem);
        }
    }

    private void addExpandables() {
        mMyTasks = (ExpandableLinearLayout) findViewById(R.id.expandable_my_tasks);
        RelativeLayout expand = (RelativeLayout) findViewById(R.id.expand_my_task);
        expand.setOnClickListener(v -> mMyTasks.toggle());

        ImageView myTasksButton = (ImageView) findViewById(R.id.expand_my_task_icon);
        Animation rotateMyTasks = AnimationUtils.loadAnimation(this, R.anim.rerotate);

        mMyTasks.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                myTasksButton.startAnimation(rotateMyTasks);
                myTasksButton.setImageDrawable(getResources().getDrawable(R.drawable.colapse));
            }

            @Override
            public void onPreClose() {
                myTasksButton.startAnimation(rotateMyTasks);
                myTasksButton.setImageDrawable(getResources().getDrawable(R.drawable.expand));
            }
        });

        mAllTasks = (ExpandableLinearLayout) findViewById(R.id.expandable_all_tasks);
        expand = (RelativeLayout) findViewById(R.id.expand_all_tasks);
        expand.setOnClickListener(v -> mAllTasks.toggle());

        ImageView allTasksButton = (ImageView) findViewById(R.id.expand_all_task_icon);
        Animation rotateAllTasks = AnimationUtils.loadAnimation(this, R.anim.rerotate);

        mAllTasks.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                allTasksButton.startAnimation(rotateAllTasks);
                allTasksButton.setImageDrawable(getResources().getDrawable(R.drawable.colapse));
            }

            @Override
            public void onPreClose() {
                allTasksButton.startAnimation(rotateAllTasks);
                allTasksButton.setImageDrawable(getResources().getDrawable(R.drawable.expand));
            }
        });

        mSettle = (ExpandableLinearLayout) findViewById(R.id.expandable_settle);
        expand = (RelativeLayout) findViewById(R.id.expand_settle);
        expand.setOnClickListener(v -> mSettle.toggle());

        ImageView settleButton = (ImageView) findViewById(R.id.expand_settle_icon);
        Animation rotateSettle = AnimationUtils.loadAnimation(this, R.anim.rerotate);

        mSettle.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                settleButton.startAnimation(rotateSettle);
                settleButton.setImageDrawable(getResources().getDrawable(R.drawable.colapse));
            }

            @Override
            public void onPreClose() {
                settleButton.startAnimation(rotateSettle);
                settleButton.setImageDrawable(getResources().getDrawable(R.drawable.expand));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.active_event_taskbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_chat) {
            Intent intent = new Intent(this, ChatRoomActivity.class);
            intent.putExtra(ChatRoomActivity.EXTRA_FRIENDS_IDS, Profile.getCurrentProfile().getId());
            intent.putExtra(ChatRoomActivity.EXTRA_FRIENDS_NAMES, Profile.getCurrentProfile().getFirstName());
            startActivity(intent);
        }
        if (id == R.id.action_add_task) {
            NewTaskDialogFragment newTaskFragment = new NewTaskDialogFragment();
            newTaskFragment.event_id = Integer.parseInt(id_event);
            FragmentManager fragmentManager = getSupportFragmentManager();
            newTaskFragment.show(fragmentManager, "newTask");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
