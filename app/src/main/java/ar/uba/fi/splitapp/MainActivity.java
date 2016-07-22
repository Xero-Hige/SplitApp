package ar.uba.fi.splitapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.pkmmte.view.CircularImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent newEventIntent = new Intent(MainActivity.this, NewEventActivity.class);
            startActivity(newEventIntent);
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        addNavHeader(navigationView);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Eventos Activos"));
        tabLayout.addTab(tabLayout.newTab().setText("Eventos Finalizados"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.new_event) {
            Utility.showMessage("No anda", Utility.getViewgroup(this), "Ok");
        } else if (id == R.id.nav_gallery) {
            Utility.showMessage("Tampoco anda", Utility.getViewgroup(this), "Ok");
        } else if (id == R.id.nav_slideshow) {
            Utility.showMessage("Basta!", Utility.getViewgroup(this), "Ok");
        } else if (id == R.id.nav_manage) {
            Utility.showMessage("Cortala!", Utility.getViewgroup(this), "Ok");
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new ActiveEventsFragment();
                case 1:
                    return new EndedEventsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
