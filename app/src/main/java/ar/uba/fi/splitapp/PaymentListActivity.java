package ar.uba.fi.splitapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PaymentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);

        LinearLayout payments = (LinearLayout) findViewById(R.id.payments_container);
        LayoutInflater inflater = getLayoutInflater();

        for (int i = 0; i < 6; i++) {
            View payment = inflater.inflate(R.layout.payment_layout, null);

            TextView name = (TextView) payment.findViewById(R.id.payment_name);
            name.setText("Metodo #" + i);

            ImageView icon = (ImageView) payment.findViewById(R.id.payment_icon);
            Glide.with(this.getApplicationContext()).load(R.drawable.logo).centerCrop().into(icon);
            payment.setOnClickListener(v -> finish());

            payments.addView(payment);
        }

    }

}
