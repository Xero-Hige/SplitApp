package ar.uba.fi.splitapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.pkmmte.view.CircularImageView;

public class EventInvitationList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_invitation_list);

        LinearLayout invites = (LinearLayout) findViewById(R.id.invitations_container);
        LayoutInflater inflater = getLayoutInflater();


        for (int i = 0; i < 10; i++) {
            View inviteeLayout = inflater.inflate(R.layout.event_invite_layout, null);

            TextView hostName = (TextView) inviteeLayout.findViewById(R.id.invite_host_name);
            hostName.setText("Te invito: Persona Nº" + (i + 1));

            TextView eventName = (TextView) inviteeLayout.findViewById(R.id.invite_event_name);
            eventName.setText("Fontefiesta Nº" + (i + 1));

            CircularImageView image = (CircularImageView) inviteeLayout.findViewById(R.id.invite_host_pic);
            FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), image, getApplicationContext());
            Button accept = (Button) inviteeLayout.findViewById(R.id.invite_accept_button);
            final int finalI = i;
            accept.setOnClickListener(v -> {
                Utility.showMessage("Aceptaste unirte al evento: 'Fontefiesta Nº" + (finalI + 1) + "'", Utility.getViewgroup(this));
                inviteeLayout.setVisibility(View.GONE);
            });

            Button decline = (Button) inviteeLayout.findViewById(R.id.invite_reject_button);
            decline.setOnClickListener(v -> {
                Utility.showMessage("Rechazaste unirte al evento: 'Fontefiesta Nº" + (finalI + 1) + "'", Utility.getViewgroup(this));
                inviteeLayout.setVisibility(View.GONE);
            });

            invites.addView(inviteeLayout);
        }
    }
}
