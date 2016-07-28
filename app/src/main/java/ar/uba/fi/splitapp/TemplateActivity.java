package ar.uba.fi.splitapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by colopreda on 24/07/16.
 */
public class TemplateActivity extends AppCompatActivity {

    private int fromTemplateNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fromTemplateNumber = getIntent().getExtras().getInt("fromTemplateNumber");

        LinearLayout templates = (LinearLayout) findViewById(R.id.eventTemplateList);

        LayoutInflater inflater = LayoutInflater.from(this);

        if (NewEventActivity.TEMPLATE_TASKS[fromTemplateNumber].length == 0) { // es el evento vacio
            View templateItem = inflater.inflate(R.layout.event_template_item, null);
            TextView text = (TextView) templateItem.findViewById(R.id.template_name);
            ImageView moreDots = (ImageView) templateItem.findViewById(R.id.startNewEvent);
            moreDots.setVisibility(View.INVISIBLE);
            text.setText("(No hay tareas aún)");
            templates.addView(templateItem);
        }

        for (String task : NewEventActivity.TEMPLATE_TASKS[fromTemplateNumber]) {   // si no esta vacio
            View templateItem = inflater.inflate(R.layout.event_template_item, null);

            TextView text = (TextView) templateItem.findViewById(R.id.template_name);
            text.setText(task);

            ImageView moreDots = (ImageView) templateItem.findViewById(R.id.startNewEvent);
            moreDots.setVisibility(View.INVISIBLE);

            templateItem.setOnClickListener(v -> {
                //Intent eventDetail = new Intent(NewEventActivity.this, NewEventDetailsActivity.class);
                //startActivity(eventDetail);
            });

            templates.addView(templateItem);
        }
    }
}
