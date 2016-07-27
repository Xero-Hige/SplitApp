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

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.pkmmte.view.CircularImageView;

/**
 * Created by colopreda on 26/07/16.
 */
public class DebtActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

        LinearLayout deudas = (LinearLayout) findViewById(R.id.eventTemplateList);

        LayoutInflater inflater = LayoutInflater.from(this);

        setSettle(deudas, inflater);


    }

    private void setSettle(LinearLayout templates, LayoutInflater inflater) {
        View templateItem = inflater.inflate(R.layout.settlement_debt_layout, null);

        CircularImageView view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        ImageView background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(this.getApplicationContext()).load(R.drawable.debt_on).centerCrop().into(background);

        final ImageView finalBackground = background;
        templateItem.setOnClickListener(v -> {
            Intent makePayment = new Intent(this, PaymentListActivity.class);
            startActivity(makePayment);
            Glide.with(this.getApplicationContext()).load(R.drawable.debt_off).centerCrop().into(finalBackground);
        });
        templates.addView(templateItem);


        templateItem = inflater.inflate(R.layout.settlement_debt_layout, null);

        view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(this.getApplicationContext()).load(R.drawable.debt_off).centerCrop().into(background);

        templateItem.setOnClickListener(v -> {
            Intent makePayment = new Intent(this, PaymentListActivity.class);
            startActivity(makePayment);
        });
        templates.addView(templateItem);


        templateItem = inflater.inflate(R.layout.settlement_acred_layout, null);

        view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(this.getApplicationContext()).load(R.drawable.settle_on).centerCrop().into(background);

        final ImageView finalBackground1 = background;
        templateItem.setOnClickListener(v -> {
            Intent makePayment = new Intent(this, PaymentListActivity.class);
            startActivity(makePayment);
            Glide.with(this.getApplicationContext()).load(R.drawable.settle_off).centerCrop().into(finalBackground1);
        });
        templates.addView(templateItem);


        templateItem = inflater.inflate(R.layout.settlement_acred_layout, null);

        view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, this.getApplicationContext());

        background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(this.getApplicationContext()).load(R.drawable.settle_off).centerCrop().into(background);

        templateItem.setOnClickListener(v -> {
            Intent makePayment = new Intent(this, PaymentListActivity.class);
            startActivity(makePayment);
        });
        templates.addView(templateItem);

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
            Intent newEventIntent = new Intent(DebtActivity.this, NewEventActivity.class);
            startActivity(newEventIntent);
        } else if (id == R.id.nav_gallery) {
            Intent newMainPageIntent = new Intent(DebtActivity.this, MainActivity.class);
            startActivity(newMainPageIntent);
            this.finish();
        } else if (id == R.id.nav_invitees) {
            Intent invitees = new Intent(DebtActivity.this, EventInvitationList.class);
            startActivity(invitees);
        } else if (id == R.id.nav_manage) {
            Intent newManageIntent = new Intent(DebtActivity.this, ManageTemplate.class);
            startActivity(newManageIntent);
        } else if (id == R.id.nav_debt) {
            //Misma clase
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
