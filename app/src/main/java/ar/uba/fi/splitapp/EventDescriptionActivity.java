package ar.uba.fi.splitapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.pkmmte.view.CircularImageView;

public class EventDescriptionActivity extends AppCompatActivity {

    private ExpandableLinearLayout my_tasks;
    private ExpandableLinearLayout all_tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        addExpandables();

        addTaskStatus();

        LinearLayout templates = (LinearLayout) findViewById(R.id.my_tasks_list);
        LayoutInflater inflater = getLayoutInflater();

        setMyTasks(templates, inflater);
    }

    private void setMyTasks(LinearLayout templates, LayoutInflater inflater) {
        for (int i = 1; i < 10; i++) {
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
                }
            });


            CircularImageView profile = (CircularImageView) templateItem.findViewById(R.id.task_profile_pic);
            FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), profile, getApplicationContext());

            templates.addView(templateItem);
        }
    }

    private void addTaskStatus() {
        LinearLayout templates = (LinearLayout) findViewById(R.id.all_tasks_list);
        LayoutInflater inflater = getLayoutInflater();

        for (int i = 1; i < 10; i++) {
            View templateItem = inflater.inflate(R.layout.task_status_layout, null);

            TextView text = (TextView) templateItem.findViewById(R.id.task_name);
            text.setText("Tarea #" + i);

            TextView date = (TextView) templateItem.findViewById(R.id.task_status);
            date.setText("Pendiente");

            CircularImageView profile = (CircularImageView) templateItem.findViewById(R.id.task_profile_pic);
            FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), profile, getApplicationContext());

            templates.addView(templateItem);
        }
    }

    private void addExpandables() {
        my_tasks = (ExpandableLinearLayout) findViewById(R.id.expandable_my_tasks);
        RelativeLayout expand = (RelativeLayout) findViewById(R.id.expand_my_task);
        expand.setOnClickListener(v -> my_tasks.toggle());

        all_tasks = (ExpandableLinearLayout) findViewById(R.id.expandable_all_tasks);
        expand = (RelativeLayout) findViewById(R.id.expand_all_tasks);
        expand.setOnClickListener(v -> all_tasks.toggle());
    }

}
