package com.ecci.Hamers;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.ecci.Hamers.Fragments.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class MainActivity extends ActionBarActivity {
    // Drawer list
    private String[] mDrawerItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    // Fragments
    QuoteListFragment quoteListFragment = new QuoteListFragment();
    UserFragment userFragment = new UserFragment();
    EventFragment eventFragment = new EventFragment();
    BeerFragment beerFragment = new BeerFragment();
    MotionFragment motionFragment = new MotionFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initDrawer();

        if (savedInstanceState == null) {
            selectItem(0);
        }
        loadData();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void initDrawer() {
        // Drawer list
        mDrawerItems = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */

                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void loadData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //reload users
        if (prefs.getString("userData", null) != null) {
            userFragment.populateList(prefs);
        } else {
            GetJson g = new GetJson(this, userFragment, GetJson.USER, prefs);
            g.execute();
        }

         //reload quotes
        if (prefs.getString("quoteData", null) != null) {
            quoteListFragment.populateList(prefs);
        } else {
            GetJson g = new GetJson(this, quoteListFragment, GetJson.QUOTE, prefs);
            g.execute();
        }
        //reload Events
        if (prefs.getString("eventData", null) != null) {
            eventFragment.populateList(prefs);
        } else {
            GetJson g = new GetJson(this, eventFragment, GetJson.EVENT, prefs);
            g.execute();
        }

        //reload Beers
        if (prefs.getString("beerData", null) != null) {
            beerFragment.populateList(prefs);
        } else {
            GetJson g = new GetJson(this, beerFragment, GetJson.BEER, prefs);
            g.execute();
        }

    }

    /**
     * Swaps fragments in the main content view
     *
     * @param position
     */
    private void selectItem(int position) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        switch (position) {
            case 0:
                transaction
                        .replace(R.id.content_frame, quoteListFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[0]);
                break;

            case 1:
                transaction
                        .replace(R.id.content_frame, userFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[1]);
                break;

            case 2:
                transaction
                        .replace(R.id.content_frame, eventFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[2]);
                break;

            case 3:
                transaction
                        .replace(R.id.content_frame, beerFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[3]);
                break;

            case 4:
                transaction
                        .replace(R.id.content_frame, motionFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[4]);
                break;

            case 5:
                transaction
                        .replace(R.id.content_frame, settingsFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[5]);
                break;
        }

        // Highlight the selected item and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * When user presses "+", start new dialog with NewQuoteFragment
     *
     * @param item
     */
    public void newQuote(MenuItem item) {
        DialogFragment newQuoteFragment = new NewQuoteFragment();
        newQuoteFragment.show(getSupportFragmentManager(), "quotes");
    }

    /**
     * Parse date
     *
     * @param dateTemp
     * @return String with parsed date
     * @throws ParseException
     */
    public static String parseDate(String dateTemp) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        return outputFormat.format(inputFormat.parse(dateTemp));
    }
}
