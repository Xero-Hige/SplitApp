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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

@TargetApi(Build.VERSION_CODES.N)
public class NewEventDetailsActivity extends AppCompatActivity {

    private TextView mDateTV;
    private TextView mTimeTV;
    private Calendar mCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long dates = System.currentTimeMillis();

        setDatePicker(dates);
        setTimePicker(dates);
        setFriendChooser();
    }

    private void setFriendChooser() {
        RelativeLayout addFriend = (RelativeLayout) findViewById(R.id.add_friend);
        addFriend.setOnClickListener(v -> {
            Intent friendListIntent = new Intent(this, FriendChooserActivity.class);
            startActivityForResult(friendListIntent, 0);
        });
    }

    private void setTimePicker(long dates) {
        mTimeTV = (TextView) findViewById(R.id.hora);
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
        mDateTV = (TextView) findViewById(R.id.editText3);
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

    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mDateTV.setText(sdf.format(mCalendar.getTime()));
    }

}
