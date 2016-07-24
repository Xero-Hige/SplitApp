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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Locale;
import java.util.function.Function;

@TargetApi(Build.VERSION_CODES.N)
public class NewEventDetailsActivity extends AppCompatActivity {

    TextView fecha;
    TextView hora;
    RelativeLayout fecha_lay;
    RelativeLayout reloj_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView calendario;
        calendario = (ImageView)findViewById(R.id.imageView);

        ImageView reloj;
        reloj = (ImageView)findViewById(R.id.imageView7);

        fecha = (TextView) findViewById(R.id.editText3);
        hora = (TextView) findViewById(R.id.hora);
        fecha_lay = (RelativeLayout) findViewById(R.id.layout_fecha);
        reloj_lay = (RelativeLayout) findViewById(R.id.layout_hora);

        long dates = System.currentTimeMillis();

        SimpleDateFormat fechasf = new SimpleDateFormat("EEE dd MMM yyyy");
        SimpleDateFormat horario = new SimpleDateFormat("HH:mm");
        String fechaString = fechasf.format(dates);
        String horarioString = horario.format(dates);
        fecha.setText(fechaString);
        hora.setText(horarioString);

        RelativeLayout addFriend = (RelativeLayout) findViewById(R.id.add_friend);
        addFriend.setOnClickListener(v -> {
            Intent friendListIntent = new Intent(this, FriendChooserActivity.class);
            startActivityForResult(friendListIntent, 0);
        });

        fecha_lay.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(NewEventDetailsActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        reloj_lay.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(NewEventDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    hora.setText( selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Elegir Hora");
            mTimePicker.show();

        });

    }

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };



    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        fecha.setText(sdf.format(myCalendar.getTime()));
    }

}
