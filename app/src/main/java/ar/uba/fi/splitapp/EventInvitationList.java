package ar.uba.fi.splitapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.pkmmte.view.CircularImageView;

public class EventInvitationList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_invitation_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        addNavHeader(navigationView);
*/

        LinearLayout invites = (LinearLayout) findViewById(R.id.invitations_container);

        LayoutInflater inflater = LayoutInflater.from(this);

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

    private void addNavHeader(NavigationView navigationView) {
        View header = navigationView.getHeaderView(0);
        CircularImageView userPic = (CircularImageView) header.findViewById(R.id.user_pic);
        Profile profile = Profile.getCurrentProfile();

        FacebookManager.fillWithUserPic(profile.getId(), userPic, getApplicationContext());
        TextView username = (TextView) header.findViewById(R.id.user_id);

        username.setText(profile.getName());

        ImageView background = (ImageView) header.findViewById(R.id.nav_background);
        FacebookManager.fillWithUserCover(profile.getId(), background, getApplicationContext());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.new_event) {
            Intent newEventIntent = new Intent(EventInvitationList.this, NewEventActivity.class);
            startActivity(newEventIntent);
        } else if (id == R.id.nav_gallery) {
            Intent newMainMenuIntent = new Intent(EventInvitationList.this, NewEventActivity.class);
            startActivity(newMainMenuIntent);
        } else if (id == R.id.nav_invitees) {
            // Nada
        } else if (id == R.id.nav_manage) {
            Intent newMainMenuIntent = new Intent(EventInvitationList.this, ManageTemplate.class);
            startActivity(newMainMenuIntent);
        } else if (id == R.id.nav_debt) {
            Intent newDebtIntent = new Intent(EventInvitationList.this, DebtActivity.class);
            startActivity(newDebtIntent);
        } else if (id == R.id.nav_settings) {
            Utility.showMessage("Deja de tocar cosas!!", Utility.getViewgroup(this), "Ok");
        } else if (id == R.id.nav_help) {
            Utility.showMessage("Deja de joder!!!!!!!!", Utility.getViewgroup(this), "Ok");
        } else if (id == R.id.nav_logout) {
            logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        LoginManager.getInstance().logOut();
        Intent mainActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}
