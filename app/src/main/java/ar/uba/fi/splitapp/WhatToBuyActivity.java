package ar.uba.fi.splitapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WhatToBuyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout templates = (LinearLayout) findViewById(R.id.eventTemplateList);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 1; i < 21; i++) {
            View templateItem = inflater.inflate(R.layout.event_template_item, null);

            TextView text = (TextView) templateItem.findViewById(R.id.template_name);
            text.setText("Item #" + String.valueOf(i));

            templateItem.setOnClickListener(v -> {
//                Intent eventDetail = new Intent(NewEventActivity.this, NewEventDetailsActivity.class);
//                startActivity(eventDetail);
            });

            templates.addView(templateItem);
        }
    }
}
