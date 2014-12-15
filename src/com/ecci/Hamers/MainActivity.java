package com.ecci.Hamers;

import android.content.res.Configuration;
import android.os.Bundle;
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


public class MainActivity extends ActionBarActivity {
    // Drawer list
    private String[] mDrawerItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

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
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Disabled want wordt anders leeg (nu we er nog niets anders
                // hebben staan dan 'Hamers'..
                //getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Disabled want wordt anders leeg (nu we er nog niets anders
                // hebben staan dan 'Hamers'..
                //getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (position) {
            case 0:
                transaction
                        .replace(R.id.content_frame, new QuoteListFragment())
                        .addToBackStack(null)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[0]);
                break;

            case 1:
                transaction
                        .replace(R.id.content_frame, new UserFragment())
                        .addToBackStack(null)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[1]);
                break;

            case 2:
                transaction
                        .replace(R.id.content_frame, new EventFragment())
                        .addToBackStack(null)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[2]);
                break;

            case 3:
                transaction
                        .replace(R.id.content_frame, new BeerFragment())
                        .addToBackStack(null)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[3]);
                break;

            case 4:
                transaction
                        .replace(R.id.content_frame, new MotionFragment())
                        .addToBackStack(null)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[4]);
                break;

            case 5:
                transaction
                        .replace(R.id.content_frame, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[5]);
                break;
        }

        // Highlight the selected item and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /** New quote */
    public void newQuote(MenuItem item) {
        DialogFragment newQuoteFragment = new NewQuoteFragment();
        newQuoteFragment.show(getSupportFragmentManager(), "quotes");
    }
}