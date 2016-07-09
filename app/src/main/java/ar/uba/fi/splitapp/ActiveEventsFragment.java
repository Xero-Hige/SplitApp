package ar.uba.fi.splitapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActiveEventsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_active_events, container, false);

        LinearLayout templates = (LinearLayout) fragment.findViewById(R.id.active_events_list);

        for (int i = 1; i < 21; i++) {
            View templateItem = inflater.inflate(R.layout.event_active_item, null);

            TextView text = (TextView) templateItem.findViewById(R.id.event_name);
            text.setText("Template #" + String.valueOf(i));

            templateItem.setOnClickListener(v -> {
                Intent eventDetail = new Intent(getContext(), NewEventDetailsActivity.class);
                startActivity(eventDetail);
            });

            Log.d("SplitApp", "Template #" + String.valueOf(i));

            templates.addView(templateItem);
        }

        return fragment;
    }

}

