package com.somrandomteam.hacklondon2016;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.somrandomteam.hacklondon2016.chat.ChatFragment;
import com.somrandomteam.hacklondon2016.chat.dummy.DummyContent;
import com.somrandomteam.hacklondon2016.event.EventFragment;
import com.somrandomteam.hacklondon2016.map.Map;
import com.somrandomteam.hacklondon2016.proximity.ProximityFragment;
import com.somrandomteam.hacklondon2016.utils.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements EventFragment.OnFragmentInteractionListener, Map.OnFragmentInteractionListener, ChatFragment.OnListFragmentInteractionListener, ProximityFragment.OnListFragmentInteractionListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Button sendMessage;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        Toast.makeText(this, "" + extras.getBoolean("Login"), Toast.LENGTH_LONG).show();
        Toast.makeText(this, extras.getString("Name"), Toast.LENGTH_LONG).show();
        Toast.makeText(this, extras.getString("EventID"), Toast.LENGTH_LONG).show();
        Toast.makeText(this, extras.getString("Details"), Toast.LENGTH_LONG).show();

        // TAB UTILS
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //


        // New Typeface. Use throughout the app.
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/mob.ttf");

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
}
