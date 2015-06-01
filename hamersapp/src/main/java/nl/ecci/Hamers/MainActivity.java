package nl.ecci.Hamers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
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
    //     public static final String baseURL = "http://192.168.100.80:3000/api/v1/";
//     Fragments
    public static final QuoteListFragment quoteListFragment = new QuoteListFragment();
    public static final UserFragment userFragment = new UserFragment();
    public static final EventFragment eventFragment = new EventFragment();
    public static final NewsFragment newsFragment = new NewsFragment();
    public static final BeerFragment beerFragment = new BeerFragment();
    private static final MotionFragment motionFragment = new MotionFragment();
    private static final SettingsFragment settingsFragment = new SettingsFragment();

    private DrawerLayout drawerLayout;
    private boolean backPressedOnce;

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
    private static void configureDefaultImageLoader(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration defaultConfiguration
                = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .build();

        // Initialize ImageLoader with configuration
        ImageLoader.getInstance().init(defaultConfiguration);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initDrawer();
        initToolbar();

        if (savedInstanceState == null) {
            selectItem("Quotes");
        }

        configureDefaultImageLoader(this);

        hasApiKey();
    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectItem(menuItem.getTitle().toString());
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });


    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.setDrawerListener(mDrawerToggle);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads all the data on startup.
     * It starts with loading the users and afterwards it calls loaddata2, which downloads the other data.
     */
    public void hasApiKey() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable key = apiKey.getText();
                if (!key.toString().equals("")) {
                    MainActivity.this.storeInMemory(key.toString());
                    MainActivity.this.showToast(getResources().getString(R.string.toast_downloading));
                    MainActivity.this.hasApiKey();
                } else {
                    MainActivity.this.showToast(getResources().getString(R.string.toast_storekeymemory));
                }
            }
        });
        alert.show();
    }

    //Show a toast with the supplied text and the supplied length
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    //Stores this key value pair in memory
    private void storeInMemory(String value) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(DataManager.APIKEYKEY, value).apply();
    }

    public void loadData2(SharedPreferences prefs, boolean auth) {
        if (auth) {
            //reload quotes
            if (prefs.getString(DataManager.QUOTEKEY, null) != null) {
                quoteListFragment.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, quoteListFragment, GetJson.QUOTEURL, prefs);
                g.execute();
            }
            //reload Events
            if (prefs.getString(DataManager.EVENTKEY, null) != null) {
                eventFragment.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, eventFragment, GetJson.EVENTURL, prefs);
                g.execute();
            }
            //reload Reviews
            if (prefs.getString(DataManager.REVIEWKEY, null) == null) {
                GetJson g = new GetJson(this, null, GetJson.REVIEWURL, prefs);
                g.execute();
            }
            //reload News
            if (prefs.getString(DataManager.NEWSKEY, null) != null) {
                newsFragment.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, newsFragment, GetJson.NEWSURL, prefs);
                g.execute();
            }
            //reload Beers
            if (prefs.getString(DataManager.BEERKEY, null) != null) {
                beerFragment.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, beerFragment, GetJson.BEERURL, prefs);
                g.execute();
            }
        } else {
            showApiKeyDialog();
        }
    }

    /**
     * Swaps fragments in the quote_list_menu content view
     *
     * @param title
     */
    private void selectItem(String title) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        hideSoftKeyboard();
        switch (title) {
            case "Quotes":
                transaction
                        .replace(R.id.content_frame, quoteListFragment)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_1));
                break;

            case "Leden":
                transaction
                        .replace(R.id.content_frame, userFragment)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_2));
                break;

            case "Activiteiten":
                transaction
                        .replace(R.id.content_frame, eventFragment)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_3));
                break;

            case "Nieuws":
                transaction
                        .replace(R.id.content_frame, newsFragment)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_4));
                break;

            case "Bieren":
                transaction
                        .replace(R.id.content_frame, beerFragment)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_5));
                break;

            case "Moties":
                transaction
                        .replace(R.id.content_frame, motionFragment)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_6));
                break;

            case "Instellingen":
                transaction
                        .replace(R.id.content_frame, settingsFragment)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_7));
                break;
        }
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
    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
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
