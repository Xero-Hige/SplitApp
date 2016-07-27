package ar.uba.fi.splitapp;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.N)
public class NewEventDetailsActivity extends AppCompatActivity {

    int PLACE_PICKER_REQUEST = 1;
    private TextView mDateTV;
    private TextView mTimeTV;
    private Calendar mCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };
    private Place selectedPlace;
    private TextView mLocationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventNew = new Intent(getApplicationContext(), NewEventActivity.class);
                startActivityForResult(eventNew, 0);
            }
        });

        long dates = System.currentTimeMillis();

        setDatePicker(dates);
        setTimePicker(dates);
        setFriendChooser();
        setLocationPicker();

    }

    private void setLocationPicker() {
        mLocationLabel = (TextView) findViewById(R.id.location_label);
        RelativeLayout location = (RelativeLayout) findViewById(R.id.location_lay);
        location.setOnClickListener(v -> {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });
    }

    private void setFriendChooser() {
        RelativeLayout addFriend = (RelativeLayout) findViewById(R.id.add_friend);
        addFriend.setOnClickListener(v -> {
            Intent friendListIntent = new Intent(this, FriendChooserActivity.class);
            startActivityForResult(friendListIntent, 0);
        });
    }

    private void setTimePicker(long dates) {
        mTimeTV = (TextView) findViewById(R.id.time_label);
        RelativeLayout reloj_lay = (RelativeLayout) findViewById(R.id.layout_hora);
        SimpleDateFormat horario = new SimpleDateFormat("HH:mm");
        String horarioString = horario.format(dates);
        mTimeTV.setText(horarioString);
        reloj_lay.setOnClickListener(v -> {
            Calendar mCurrentTime = Calendar.getInstance();
            int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mCurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(NewEventDetailsActivity.this,
                    (timePicker, selectedHour, selectedMinute) -> mTimeTV.setText(
                            selectedHour + ":" + selectedMinute), hour, minute, true);
            mTimePicker.setTitle("Elegir Hora");
            mTimePicker.show();

        });
    }

    private void setDatePicker(long dates) {
        mDateTV = (TextView) findViewById(R.id.date_label);
        RelativeLayout fecha_lay = (RelativeLayout) findViewById(R.id.layout_fecha);
        SimpleDateFormat fechasf = new SimpleDateFormat("EEE dd MMM yyyy");
        String fechaString = fechasf.format(dates);
        mDateTV.setText(fechaString);
        fecha_lay.setOnClickListener(v -> {
            new DatePickerDialog(NewEventDetailsActivity.this,
                    date,
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_event_taskbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create) {
            JSONObject obj = new JSONObject();
            String nameEvent = ((EditText)findViewById(R.id.editText)).getText().toString();
            String dateEvent =((TextView) findViewById(R.id.date_label)).getText().toString();
            String dateString = "";

            try {
                Date date_class = new SimpleDateFormat("EEE dd MMM yyyy").parse(dateEvent);
                dateString = new SimpleDateFormat("yyyy-MM-dd").format(date_class);
            } catch (ParseException e) {
                e.printStackTrace();
            }



            String timeEvent = (((TextView) findViewById(R.id.time_label)).getText().toString()+":00");
            String whenEvent = dateString + " " + timeEvent;
            String latEvent  = Double.toString(selectedPlace.getLatLng().latitude);
            String longEvent = Double.toString(selectedPlace.getLatLng().longitude);

            try {
                obj.put("name", nameEvent);
                obj.put("when", whenEvent);
                obj.put("lat", latEvent);
                obj.put("long", longEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(obj);


            ServerHandler.executePost(ServerHandler.EVENT_LIST, obj, result -> {
                //onSucces.execute(result);
                if (result == null) {
                    //onError.execute(null);
                } else try {
                        JSONObject callback = result.getJSONObject("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                selectedPlace = PlacePicker.getPlace(data, this);
                Utility.showMessage(selectedPlace.getAddress().toString() + "\n" + selectedPlace.getLatLng().toString(), Utility.getViewgroup(this));
                mLocationLabel.setText(selectedPlace.getAddress().toString());
            }
        }
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mDateTV.setText(sdf.format(mCalendar.getTime()));
    }


}
