package nl.ecci.Hamers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import nl.ecci.Hamers.Beers.*;
import nl.ecci.Hamers.Events.EventFragment;
import nl.ecci.Hamers.Events.NewEventActivity;
import nl.ecci.Hamers.Helpers.GetJson;
import nl.ecci.Hamers.Helpers.JSONHelper;
import nl.ecci.Hamers.Quotes.NewQuoteFragment;
import nl.ecci.Hamers.Quotes.QuoteListFragment;
import nl.ecci.Hamers.Users.UserFragment;

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

    /**
     * Loads all the data on startup.
     * It starts with loading the users and afterwards it calls loaddata2, which downloads the other data.
     */
    public void loadData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //reload users
        if (prefs.getString("apikey", null) != null) {
            if (prefs.getString(JSONHelper.USERKEY, null) != null) {
                userFragment.populateList(prefs);
                loadData2(prefs);
            } else {
                GetJson g = new GetJson(this, userFragment, GetJson.USERURL, prefs, true);
                g.execute();
            }
        }else{
            showApiKeyDialog();
        }
    }

    //Show the dialog for entering the apikey on startup
    private void showApiKeyDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        Activity a = this;
        alert.setTitle(getString(R.string.apikeydialogtitle));
        alert.setMessage(getString(R.string.apikeydialogmessage));
        final EditText apiKey = new EditText(this);
        apiKey.setHint(getString(R.string.apikey_hint));
        alert.setView(apiKey);
        alert.setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable key = apiKey.getText();
                if (!key.toString().equals("")) {
                    storeInMemory("apikey", key.toString());
                    showToast(getResources().getString(R.string.toast_downloading), Toast.LENGTH_LONG);
                    loadData();
                } else {
                    showToast(getResources().getString(R.string.toast_storekeymemory), Toast.LENGTH_LONG);
                }
            }
        });
        alert.setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                showToast(getResources().getString(R.string.toast_storekeymemory), Toast.LENGTH_LONG);
            }
        });
        alert.show();
    }

    //Show a toast with the supplied text and the supplied length
    private void showToast(String text, int length){
        Toast.makeText(this, text, length).show();
    }

    //Stores this key value pair in memory
    private void storeInMemory(String key, String value){
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(key, value).apply();
    }


    public void loadData2(SharedPreferences prefs) {
        System.out.println("loaddata2 called");
        //reload quotes
        if (prefs.getString(JSONHelper.QUOTEKEY, null) != null) {
            quoteListFragment.populateList(prefs);
        } else {
            GetJson g = new GetJson(this, quoteListFragment, GetJson.QUOTEURL, prefs, false);
            g.execute();
        }
        //reload Events
        if (prefs.getString(JSONHelper.EVENTKEY, null) != null) {
            eventFragment.populateList(prefs);
        } else {
            GetJson g = new GetJson(this, eventFragment, GetJson.EVENTURL, prefs, false);
            g.execute();
        }

        //reload Beers
        if (prefs.getString(JSONHelper.BEERKEY, null) != null) {
            beerFragment.populateList(prefs);
        } else {
            GetJson g = new GetJson(this, beerFragment, GetJson.BEERURL, prefs, false);
            g.execute();
        }
    }

    /**
     * Swaps fragments in the quote_list_menu content view
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
     * When user presses "+" in QuoteListFragment, start new dialog with NewQuoteFragment
     * @param item
     */
    public void newQuote(MenuItem item) {
        DialogFragment newQuoteFragment = new NewQuoteFragment();
        newQuoteFragment.show(getSupportFragmentManager(), "quotes");
    }

    /**
     * When user presses "+" in EventFragment, start new dialog with NewEventActivity
     * @param item
     */
    public void newEvent(MenuItem item) {
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
    }

    /**
     * When user presses "+" in BeerFragment, start new dialog with NewBeerActivity
     * @param item
     */
    public void newBeer(MenuItem item) {
        Intent intent = new Intent(this, NewBeerActivity.class);
        startActivity(intent);
    }

    /**
     * Parse date
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
