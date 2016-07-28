package ar.uba.fi.splitapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewEventActivity extends AppCompatActivity {

    static final String[] TEMPLATE_NAMES = {"Evento vacío", "Asado con amigos", "Reunión laboral", "Cumpleaños"};
    static final String[][] TEMPLATE_TASKS = {
            {},
            {"Carne", "Coca", "Birra", "Helado"},
            {"Medialunas", "Coffee", "Leche", "Mate", "Galletitas"},
            {"Torta", "Bebidas", "Velitas", "Snacks"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(view -> {
            Intent eventMain = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(eventMain, 0);
        });

        displayTemplates();
    }


    private void displayTemplates() {
        LinearLayout templates = (LinearLayout) findViewById(R.id.eventTemplateList);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < TEMPLATE_NAMES.length; i++) {
            String templateName = TEMPLATE_NAMES[i];
            View templateItem = inflater.inflate(R.layout.event_template_item, null);

            TextView text = (TextView) templateItem.findViewById(R.id.template_name);
            text.setText(templateName);

            final int finalI = i;
            templateItem.setOnClickListener(v -> {
                Intent eventDetail = new Intent(NewEventActivity.this, NewEventDetailsActivity.class);
                eventDetail.putExtra("fromTemplateNumber", finalI);
                startActivity(eventDetail);
            });

            templates.addView(templateItem);
        }
    }

}
