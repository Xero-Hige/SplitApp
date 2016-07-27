package ar.uba.fi.splitapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class ActiveEventsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FacebookManager.checkInit(this.getActivity());

        View fragment = inflater.inflate(R.layout.fragment_active_events, container, false);

        LinearLayout templates = (LinearLayout) fragment.findViewById(R.id.active_events_list);

        //Mock

        getEvents(inflater, templates);


        /*for (int i = 1; i < 21; i++) {
            templateItem = inflater.inflate(R.layout.event_active_item, null);

            TextView text = (TextView) templateItem.findViewById(R.id.event_name);
            text.setText("Evento #" + i);

            TextView date = (TextView) templateItem.findViewById(R.id.event_date);
            date.setText(DateFormat.getDateInstance().format(new Date()));

            templateItem.setOnClickListener(v -> {
                Intent eventDetail = new Intent(getContext(), EventDescriptionActivity.class);
                startActivity(eventDetail);
            });

            templates.addView(templateItem);

        }*/


        return fragment;
    }

    private void getEvents(LayoutInflater inflater, LinearLayout templates) {



        ServerHandler.executeGet(ServerHandler.EVENT_LIST, "", "", result -> {
            //onSucces.execute(result);
            if (result == null) {
                //onError.execute(null);
            } else try {
                JSONArray events = result.getJSONArray("data");
                SplitAppLogger.writeLog(1,"Longitud: " + events.length());
                for (int i = 0; i < events.length(); i++) {
                    SplitAppLogger.writeLog(1,"I vale: " + i);
                    View templateItem = inflater.inflate(R.layout.event_active_item, null);

                    assert templateItem != null;
                    TextView text = (TextView) templateItem.findViewById(R.id.event_name);

                    assert text != null;
                    String name_event = events.getJSONObject(i).getString("name");
                    text.setText(name_event);

                    TextView date = (TextView) templateItem.findViewById(R.id.event_date);
                    date.setText(DateFormat.getDateInstance().format(new Date()));

                    templateItem.setOnClickListener(v -> {
                        Intent eventDetail = new Intent(getContext(), EventDescriptionActivity.class);
                        startActivity(eventDetail);
                    });

                    templates.addView(templateItem);
                }
                // PARSE
            } catch (JSONException e) {
                //onError.execute(null);
                return;
            }
        });
    }

}

