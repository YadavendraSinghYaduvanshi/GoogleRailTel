package com.cpm.googlerailtel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.googlerailtel.R;
import com.cpm.googlerailtel.constant.CommonString;
import com.cpm.googlerailtel.constant.SharedPreferenceUtility;
import com.cpm.googlerailtel.database.GoogleRailTelDB;
import com.cpm.googlerailtel.xmlGetterSetter.ImageDataGetterSetter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    TextView tv_username,tv_usertype;
    private SharedPreferenceUtility preferences;
    String user_name,user_type;
    String  visit_date;
    private ArrayList<ImageDataGetterSetter> imageDataGetterSetters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main,
                navigationView, false);
        navigationView.addHeaderView(headerView);
        preferences = SharedPreferenceUtility.getInstance(this);

        user_name = preferences.getStringData(CommonString.KEY_USERNAME);
        user_type = preferences.getStringData(CommonString.KEY_USER_TYPE);

        tv_username = (TextView) headerView.findViewById(R.id.txt_user);
        tv_usertype = (TextView) headerView.findViewById(R.id.txt_usertype);

        tv_username.setText(user_name);
        tv_usertype.setText(user_type);

        preferences = SharedPreferenceUtility.getInstance(this);
        visit_date = preferences.getStringData(CommonString.KEY_DATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } /*else {
            super.onBackPressed();
        }*/
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily_entry) {
            // Handle the camera action
            Intent startDownload = new Intent(this, CaptureImageActivity.class);
            startActivity(startDownload);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            // Handle the camera action
        }else if (id == R.id.nav_upload) {
            GoogleRailTelDB db = new GoogleRailTelDB(MainActivity.this);
            db.open();
            imageDataGetterSetters =  db.getUplaodImageData(visit_date);
            if(imageDataGetterSetters.size()>0){
                Intent intent = new Intent(MainActivity.this,UploadActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "No Data for Upload", Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.nav_exit) {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
