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

        // HARDCODEO DE MEDIOS DE PAGO

        View payment = inflater.inflate(R.layout.payment_layout, null);

        TextView name = (TextView) payment.findViewById(R.id.payment_name);
        name.setText("Efectivo");

        ImageView icon = (ImageView) payment.findViewById(R.id.payment_icon);
        Glide.with(this.getApplicationContext()).load(R.drawable.cash).centerCrop().into(icon);

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




        View payment2 = inflater.inflate(R.layout.payment_layout, null);

        TextView name2 = (TextView) payment2.findViewById(R.id.payment_name);
        name2.setText("Paypal");

        ImageView icon2 = (ImageView) payment2.findViewById(R.id.payment_icon);
        Glide.with(this.getApplicationContext()).load(R.drawable.paypal).centerCrop().into(icon2);

        payment2.setOnClickListener(v -> {
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

        payments.addView(payment2);



        View payment3 = inflater.inflate(R.layout.payment_layout, null);

        TextView name3 = (TextView) payment3.findViewById(R.id.payment_name);
        name3.setText("Transferencia Bancaria");

        ImageView icon3 = (ImageView) payment3.findViewById(R.id.payment_icon);
        Glide.with(this.getApplicationContext()).load(R.drawable.trans_bancaria).centerCrop().into(icon3);

        payment3.setOnClickListener(v -> {
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

        payments.addView(payment3);





        View payment4 = inflater.inflate(R.layout.payment_layout, null);

        TextView name4 = (TextView) payment4.findViewById(R.id.payment_name);
        name4.setText("Mercado Pago");

        ImageView icon4 = (ImageView) payment4.findViewById(R.id.payment_icon);
        Glide.with(this.getApplicationContext()).load(R.drawable.mercadopago).centerCrop().into(icon4);

        payment4.setOnClickListener(v -> {
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

        payments.addView(payment4);





/*



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
        }*/

    }

}
