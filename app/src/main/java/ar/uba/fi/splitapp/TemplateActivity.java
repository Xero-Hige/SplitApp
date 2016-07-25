package ar.uba.fi.splitapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by colopreda on 24/07/16.
 */
public class TemplateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout templates = (LinearLayout) findViewById(R.id.eventTemplateList);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 1; i < 5; i++) {
            View templateItem = inflater.inflate(R.layout.event_template_item, null);

            TextView text = (TextView) templateItem.findViewById(R.id.template_name);
            text.setText("Elemento" + String.valueOf(i));

            templateItem.setOnClickListener(v -> {
                //Intent eventDetail = new Intent(NewEventActivity.this, NewEventDetailsActivity.class);
                //startActivity(eventDetail);
            });

            templates.addView(templateItem);
        }
    }
}