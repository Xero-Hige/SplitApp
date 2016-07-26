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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.pkmmte.view.CircularImageView;

/**
 * Created by colopreda on 24/07/16.
 */
public class ManageTemplate extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookManager.checkInit(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_template_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        addNavHeader(navigationView);

        LinearLayout templates = (LinearLayout) findViewById(R.id.eventTemplateList);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 1; i < 21; i++) {
            View templateItem = inflater.inflate(R.layout.event_template_item, null);

            TextView text = (TextView) templateItem.findViewById(R.id.template_name);
            text.setText("Template #" + String.valueOf(i));

            templateItem.setOnClickListener(v -> {
                Intent eventDetail = new Intent(ManageTemplate.this, TemplateActivity.class);
                startActivity(eventDetail);
            });

            templates.addView(templateItem);
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
            Intent newEventIntent = new Intent(ManageTemplate.this, NewEventActivity.class);
            startActivity(newEventIntent);
        } else if (id == R.id.nav_gallery) {
            Utility.showMessage("Tampoco anda", Utility.getViewgroup(this), "Ok");
        } else if (id == R.id.nav_slideshow) {
            Utility.showMessage("Basta!", Utility.getViewgroup(this), "Ok");
        } else if (id == R.id.nav_manage) {
            // Misma Clase
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
