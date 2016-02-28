package com.somrandomteam.hacklondon2016;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.HttpGet;
import com.somrandomteam.hacklondon2016.chat.ChatFragment;
import com.somrandomteam.hacklondon2016.event.EventFragment;
import com.somrandomteam.hacklondon2016.map.Map;
import com.somrandomteam.hacklondon2016.proximity.ProximityFragment;
import com.somrandomteam.hacklondon2016.utils.GPSTracker;
import com.somrandomteam.hacklondon2016.utils.Globals;
import com.somrandomteam.hacklondon2016.utils.ViewPagerAdapter;

import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class MainActivity extends AppCompatActivity implements EventFragment.OnFragmentInteractionListener, Map.OnFragmentInteractionListener, ChatFragment.OnListFragmentInteractionListener, ProximityFragment.OnListFragmentInteractionListener {

    private GPSTracker gps;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String baseUrl;

    private Button sendMessage;
    private EditText message;

    private static final int FINE_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseUrl = HackApplication.getSecret("base.url");
        Bundle extras = getIntent().getExtras();
        Globals.event = extras.getString("EventID");
        Globals.user_id = extras.getString("Name");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_CODE);

        gps = new GPSTracker(this);

        // TAB UTILS
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            finish();
            Intent loginAgain = new Intent(MainActivity.this, Login.class);
            startActivity(loginAgain);
            return true;
        }

        if (id == R.id.exit) {
            finish();
            return true;
        }

        if (id == R.id.reset) {
            try {
                new DefaultHttpClient().execute(new HttpGet(baseUrl + "clear"));
                Toast.makeText(this, "Database Cleared", Toast.LENGTH_SHORT).show();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EventFragment(), "Event");
        adapter.addFragment(new Map(), "Map");
        adapter.addFragment(new ChatFragment(), "Chat");
        adapter.addFragment(new ProximityFragment(), "Nearby");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onListFragmentInteraction(com.somrandomteam.hacklondon2016.chat.dummy.DummyContent.DummyItem item) {

    }

    @Override
    public void onListFragmentInteraction(com.somrandomteam.hacklondon2016.proximity.dummy.DummyContent.DummyItem item) {

    }

    @Override
    public void onEventUpdate(Uri uri) {

    }

    @Override
    public void onMapUpdate(Uri uri) {

    }
}
