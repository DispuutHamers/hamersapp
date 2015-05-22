package nl.ecci.Hamers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import nl.ecci.Hamers.Beers.BeerFragment;
import nl.ecci.Hamers.Beers.NewBeerActivity;
import nl.ecci.Hamers.Events.EventFragment;
import nl.ecci.Hamers.Events.NewEventActivity;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.GetJson;
import nl.ecci.Hamers.News.NewNewsActivity;
import nl.ecci.Hamers.News.NewsFragment;
import nl.ecci.Hamers.Quotes.NewQuoteFragment;
import nl.ecci.Hamers.Quotes.QuoteListFragment;
import nl.ecci.Hamers.Users.UserFragment;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {
    // URL
    public static final String baseURL = "https://zondersikkel.nl/api/v1/";
    //    public static final String baseURL = "http://192.168.100.80:3000/api/v1/";
    // Fragments
    public static QuoteListFragment quoteListFragment = new QuoteListFragment();
    public static UserFragment userFragment = new UserFragment();
    public static EventFragment eventFragment = new EventFragment();
    public static NewsFragment newsFragment = new NewsFragment();
    public static BeerFragment beerFragment = new BeerFragment();
    public static MotionFragment motionFragment = new MotionFragment();
    public static SettingsFragment settingsFragment = new SettingsFragment();

    private SharedPreferences prefs;

    // Drawer list
    private String[] mDrawerItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean backPressedOnce;

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

        configureDefaultImageLoader(this);

        hasApiKey();
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
        return super.onOptionsItemSelected(item);
    }

    public void initDrawer() {
        // Drawer list
        mDrawerItems = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new NavigationDrawerAdapter(this, mDrawerItems));
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

    /**
     * Loads all the data on startup.
     * It starts with loading the users and afterwards it calls loaddata2, which downloads the other data.
     */
    public void hasApiKey() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("apikey", null) == null) {
            showApiKeyDialog();
        }
    }

    /**
     * Show the dialog for entering the apikey on startup
     */
    private void showApiKeyDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.apikeydialogtitle));
        alert.setMessage(getString(R.string.apikeydialogmessage));
        final EditText apiKey = new EditText(this);
        apiKey.setHint(getString(R.string.apikey_hint));
        alert.setView(apiKey);
        alert.setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable key = apiKey.getText();
                if (!key.toString().equals("")) {
                    storeInMemory(DataManager.APIKEYKEY, key.toString());
                    showToast(getResources().getString(R.string.toast_downloading), Toast.LENGTH_LONG);
                    hasApiKey();
                } else {
                    showToast(getResources().getString(R.string.toast_storekeymemory), Toast.LENGTH_LONG);
                }
            }
        });
        alert.show();
    }

    //Show a toast with the supplied text and the supplied length
    private void showToast(String text, int length) {
        Toast.makeText(this, text, length).show();
    }

    //Stores this key value pair in memory
    private void storeInMemory(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(key, value).apply();
    }

    public void loadData2(SharedPreferences prefs, boolean auth) {
        if (auth) {
            //reload quotes
            if (prefs.getString(DataManager.QUOTEKEY, null) != null) {
                quoteListFragment.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, quoteListFragment, GetJson.QUOTEURL, prefs, false);
                g.execute();
            }
            //reload Events
            if (prefs.getString(DataManager.EVENTKEY, null) != null) {
                eventFragment.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, eventFragment, GetJson.EVENTURL, prefs, false);
                g.execute();
            }
            //reload Reviews
            if (prefs.getString(DataManager.REVIEWKEY, null) == null) {
                GetJson g = new GetJson(this, null, GetJson.REVIEWURL, prefs, false);
                g.execute();
            }
            //reload News
            if (prefs.getString(DataManager.NEWSKEY, null) != null) {
                newsFragment.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, newsFragment, GetJson.NEWSURL, prefs, false);
                g.execute();
            }
            //reload Beers
            if (prefs.getString(DataManager.BEERKEY, null) != null) {
                beerFragment.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, beerFragment, GetJson.BEERURL, prefs, false);
                g.execute();
            }
        } else {
            showApiKeyDialog();
        }
    }

    /**
     * Swaps fragments in the quote_list_menu content view
     *
     * @param position
     */
    private void selectItem(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        hideSoftKeyboard();
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
                        .replace(R.id.content_frame, newsFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[3]);
                break;

            case 4:
                transaction
                        .replace(R.id.content_frame, beerFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[4]);
                break;

            case 5:
                transaction
                        .replace(R.id.content_frame, motionFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[5]);
                break;

            case 6:
                transaction
                        .replace(R.id.content_frame, settingsFragment)
                        .commit();
                setTitle(getResources().getStringArray(R.array.drawer_array)[6]);
                break;
        }

        // Highlight the selected item and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * When user presses "+" in QuoteListFragment, start new dialog with NewQuoteFragment
     *
     * @param view
     */
    public void newQuote(View view) {
        DialogFragment newQuoteFragment = new NewQuoteFragment();
        newQuoteFragment.show(getSupportFragmentManager(), "quotes");
    }

    /**
     * When user presses "+" in EventFragment, start new dialog with NewEventActivity
     *
     * @param view
     */
    public void newEvent(View view) {
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
    }

    /**
     * When user presses "+" in BeerFragment, start new dialog with NewBeerActivity
     *
     * @param view
     */
    public void newBeer(View view) {
        Intent intent = new Intent(this, NewBeerActivity.class);
        startActivity(intent);
    }

    /**
     * When user presses "+" in NewsFragment, start new dialog with NewNewsActivity
     *
     * @param view
     */
    public void newNews(View view) {
        Intent intent = new Intent(this, NewNewsActivity.class);
        startActivity(intent);
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {    //replace this with actual function which returns if the drawer is open
            mDrawerLayout.closeDrawer(Gravity.LEFT);     // replace this with actual function which closes drawer
        } else {
            if (backPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.backPressedOnce = true;
            Toast.makeText(this, "Klik nog een keer op 'back' om af te sluiten.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedOnce = false;
                }
            }, 2000);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Parse date
     *
     * @param dateTemp
     * @return String with parsed date
     * @throws java.text.ParseException
     */
    public static String parseDate(String dateTemp) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        return outputFormat.format(inputFormat.parse(dateTemp));
    }

    /**
     * Setup of default ImageLoader configuration (Universal Image Loader)
     * https://github.com/nostra13/Android-Universal-Image-Loader
     *
     * @param context
     */
    public static void configureDefaultImageLoader(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration defaultConfiguration
                = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .build();

        // Initialize ImageLoader with configuration
        ImageLoader.getInstance().init(defaultConfiguration);
    }
}
