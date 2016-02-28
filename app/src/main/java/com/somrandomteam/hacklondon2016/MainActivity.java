package com.somrandomteam.hacklondon2016;

import android.Manifest;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.somrandomteam.hacklondon2016.chat.ChatFragment;
import com.somrandomteam.hacklondon2016.event.EventFragment;
import com.somrandomteam.hacklondon2016.map.Map;
import com.somrandomteam.hacklondon2016.proximity.ProximityFragment;
import com.somrandomteam.hacklondon2016.utils.GPSTracker;
import com.somrandomteam.hacklondon2016.utils.Globals;
import com.somrandomteam.hacklondon2016.utils.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class MainActivity extends AppCompatActivity implements EventFragment.OnFragmentInteractionListener, Map.OnFragmentInteractionListener, ChatFragment.OnListFragmentInteractionListener, ProximityFragment.OnListFragmentInteractionListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Button sendMessage;
    private EditText message;

    private static final int FINE_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        Globals.event = extras.getString("EventID");
        Globals.user_id = extras.getString("Name");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_CODE);

        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            GPSUpdater updater = new GPSUpdater(gps);
            updater.execute();
        }

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

        if (id == R.id.action_settings) {
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

    public class GPSUpdater extends AsyncTask<String, String, Boolean> {

        GPSTracker gps;

        public GPSUpdater(GPSTracker gps) {
            this.gps = gps;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            while(gps.canGetLocation()) {
                Double lat = gps.getLatitude();
                Double lon = gps.getLongitude();

                List<NameValuePair> data = new ArrayList<>();
                data.add(new BasicNameValuePair("latitude", lat.toString()));
                data.add(new BasicNameValuePair("longitude", lon.toString()));

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(Globals.baseEvent +  Globals.event + "/users/" + Globals.user_id + "/loc");
                try {
                    post.setEntity(new UrlEncodedFormEntity(data));
                    //client.execute(post);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return false;
        }
    }
}
