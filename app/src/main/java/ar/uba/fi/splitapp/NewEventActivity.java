package ar.uba.fi.splitapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout templates = (LinearLayout) findViewById(R.id.eventTemplateList);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 1; i < 21; i++) {
            View templateItem = inflater.inflate(R.layout.event_template_item, null);

            TextView text = (TextView) templateItem.findViewById(R.id.template_name);
            text.setText("Template #" + String.valueOf(i));

            templateItem.setOnClickListener(v -> {
                Intent eventDetail = new Intent(NewEventActivity.this, NewEventDetailsActivity.class);
                startActivity(eventDetail);
            });

            Log.d("SplitApp", "Template #" + String.valueOf(i));

            templates.addView(templateItem);
        }
    }

}
