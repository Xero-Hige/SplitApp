package ar.uba.fi.splitapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class PaymentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);

        LinearLayout payments = (LinearLayout) findViewById(R.id.payments_container);
        LayoutInflater inflater = getLayoutInflater();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        for (int i = 0; i < 6; i++) {
            View payment = inflater.inflate(R.layout.payment_layout, null);

            TextView name = (TextView) payment.findViewById(R.id.payment_name);
            name.setText("Metodo #" + i);

            ImageView icon = (ImageView) payment.findViewById(R.id.payment_icon);
            Glide.with(this.getApplicationContext()).load(R.drawable.logo).centerCrop().into(icon);
            payment.setOnClickListener(v -> {
                Toast toast = Toast.makeText(this, "Pagado", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                Bundle event_id_passed = getIntent().getExtras();
                String id_event = "error";
                if (event_id_passed != null) {
                    id_event = event_id_passed.getString("id");
                }
                if (id_event != "error"){
                    Intent backMain = new Intent(this, EventDescriptionActivity.class);
                    backMain.putExtra("id",id_event);
                    startActivity(backMain);
                } else {
                    Intent backMain = new Intent(this, DebtActivity.class);
                    startActivity(backMain);
                }


            });

            payments.addView(payment);
        }

    }

}
