package ar.uba.fi.splitapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActiveEventsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FacebookManager.checkInit(this.getActivity());

        View fragment = inflater.inflate(R.layout.fragment_active_events, container, false);

        LinearLayout templates = (LinearLayout) fragment.findViewById(R.id.active_events_list);

        getEvents(inflater, templates);


        return fragment;
    }

    private void getEvents(LayoutInflater inflater, LinearLayout templates) {


        ServerHandler.executeGet(ServerHandler.EVENT_LIST,  Profile.getCurrentProfile().getId(), "", result -> {
            //onSucces.execute(result);
            if (result == null) {
                //onError.execute(null);
            } else try {
                JSONArray events = result.getJSONArray("data");
                for (int i = 0; i < events.length(); i++) {
                    View templateItem = inflater.inflate(R.layout.event_active_item, null);

                    assert templateItem != null;
                    TextView text = (TextView) templateItem.findViewById(R.id.event_name);

                    assert text != null;
                    String name_event = events.getJSONObject(i).getString("name");
                    text.setText(name_event);

                    String date_finish_str = events.getJSONObject(i).getString("when");

                    TextView date = (TextView) templateItem.findViewById(R.id.event_date);
                    date.setText(date_finish_str);

                    String event_id = events.getJSONObject(i).getString("id");

                    templateItem.setOnClickListener(v -> {
                        Intent eventDetail = new Intent(getContext(), EventDescriptionActivity.class);
                        eventDetail.putExtra("id", event_id);
                        startActivity(eventDetail);
                    });

                    Date date_past = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date_finish_str);

                    Calendar cal = Calendar.getInstance(); // creates calendar
                    cal.setTime(date_past); // sets calendar time/date
                    cal.add(Calendar.HOUR, 12);
                    date_past = cal.getTime();

                    Date date_finish = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date_finish_str);

                    boolean esActivo = date_past.after(new Date());

                    if (esActivo) templates.addView(templateItem);
                }
                // PARSE
            } catch (JSONException e) {
                //onError.execute(null);
                return;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

}

