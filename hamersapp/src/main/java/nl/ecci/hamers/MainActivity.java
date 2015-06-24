package nl.ecci.hamers;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import nl.ecci.hamers.beers.BeerFragment;
import nl.ecci.hamers.beers.NewBeerActivity;
import nl.ecci.hamers.events.EventFragment;
import nl.ecci.hamers.events.NewEventActivity;
import nl.ecci.hamers.gcm.RegistrationIntentService;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.GetJson;
import nl.ecci.hamers.helpers.Utils;
import nl.ecci.hamers.news.NewNewsActivity;
import nl.ecci.hamers.news.NewsFragment;
import nl.ecci.hamers.quotes.NewQuoteFragment;
import nl.ecci.hamers.quotes.QuoteFragment;
import nl.ecci.hamers.users.UserFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // URL
    public static final String baseURL = "https://zondersikkel.nl/api/v1/";
    //        public static final String baseURL = "http://192.168.100.80:3000/api/v1/";
    //     Fragments
    public static final QuoteFragment QUOTE_FRAGMENT = new QuoteFragment();
    public static final UserFragment USER_FRAGMENT = new UserFragment();
    public static final EventFragment EVENT_FRAGMENT = new EventFragment();
    public static final NewsFragment NEWS_FRAGMENT = new NewsFragment();
    public static final BeerFragment BEER_FRAGMENT = new BeerFragment();
    public static final MotionFragment MOTION_FRAGMENT = new MotionFragment();
    public static final SettingsFragment SETTINGS_FRAGMENT = new SettingsFragment();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static SharedPreferences prefs;

    private LinearLayout parentLayout;
    private DrawerLayout drawerLayout;
    private boolean backPressedOnce;
    // GCM
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

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
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
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

        parentLayout = (LinearLayout) findViewById(R.id.main_parent);

        initDrawer();
        initToolbar();

        if (savedInstanceState == null) {
            selectItem(R.id.navigation_item_1);
        }

        configureDefaultImageLoader(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences prefs =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = prefs
                        .getBoolean(SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    System.out.println(getString(R.string.gcm_send_message));
                } else {
                    System.out.println(getString(R.string.token_error_message));
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        fillHeader();

        hasApiKey();
    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectItem(menuItem.getItemId());
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
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                System.out.println("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Loads all the data on startup.
     * It starts with loading the users and afterwards it calls loaddata2, which downloads the other data.
     */
    public void hasApiKey() {
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
                    MainActivity.this.showSnackbar(getResources().getString(R.string.snackbar_downloading));
                    MainActivity.this.hasApiKey();
                } else {
                    MainActivity.this.showSnackbar(getResources().getString(R.string.snackbar_storekeymemory));
                }
            }
        });
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    //Show a Snackbar with the supplied text and the supplied length
    private void showSnackbar(String text) {
        Snackbar.make(parentLayout, text, Snackbar.LENGTH_LONG).show();
    }

    //Stores this key value pair in memory
    private void storeInMemory(String value) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(DataManager.APIKEYKEY, value).apply();
    }

    public void loadData2(SharedPreferences prefs, boolean auth) {
        if (auth) {
            //reload quotes
            if (prefs.getString(DataManager.QUOTEKEY, null) != null) {
                QUOTE_FRAGMENT.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, QUOTE_FRAGMENT, GetJson.QUOTEURL, prefs);
                g.execute();
            }
            //reload Events
            if (prefs.getString(DataManager.EVENTKEY, null) != null) {
                EVENT_FRAGMENT.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, EVENT_FRAGMENT, GetJson.EVENTURL, prefs);
                g.execute();
            }
            //reload Reviews
            if (prefs.getString(DataManager.REVIEWKEY, null) == null) {
                GetJson g = new GetJson(this, null, GetJson.REVIEWURL, prefs);
                g.execute();
            }
            //reload News
            if (prefs.getString(DataManager.NEWSKEY, null) != null) {
                NEWS_FRAGMENT.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, NEWS_FRAGMENT, GetJson.NEWSURL, prefs);
                g.execute();
            }
            //reload Beers
            if (prefs.getString(DataManager.BEERKEY, null) != null) {
                BEER_FRAGMENT.populateList(prefs);
            } else {
                GetJson g = new GetJson(this, BEER_FRAGMENT, GetJson.BEERURL, prefs);
                g.execute();
            }
            fillHeader();
        } else {
            showApiKeyDialog();
        }
    }

    /**
     * Swaps fragments in the quote_list_menu content view
     *
     * @param id
     */
    private void selectItem(int id) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        hideSoftKeyboard();
        switch (id) {
            case R.id.navigation_item_1:
                transaction
                        .replace(R.id.content_frame, QUOTE_FRAGMENT)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_1));
                break;

            case R.id.navigation_item_2:
                transaction
                        .replace(R.id.content_frame, USER_FRAGMENT)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_2));
                break;

            case R.id.navigation_item_3:
                transaction
                        .replace(R.id.content_frame, EVENT_FRAGMENT)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_3));
                break;

            case R.id.navigation_item_4:
                transaction
                        .replace(R.id.content_frame, NEWS_FRAGMENT)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_4));
                break;

            case R.id.navigation_item_5:
                transaction
                        .replace(R.id.content_frame, BEER_FRAGMENT)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_5));
                break;

            case R.id.navigation_item_6:
                transaction
                        .replace(R.id.content_frame, MOTION_FRAGMENT)
                        .commit();
                setTitle(getResources().getString(R.string.navigation_item_6));
                break;

            case R.id.navigation_item_7:
                transaction
                        .replace(R.id.content_frame, SETTINGS_FRAGMENT)
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
        Snackbar.make(parentLayout, getResources().getString(R.string.press_back_again), Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, 2000);
    }

    public void fillHeader() {
        JSONArray whoami = DataManager.getJsonArray(prefs, DataManager.WHOAMIKEY);

        try {
            if (whoami != null) {
                JSONObject user = whoami.getJSONObject(0);

                TextView userName = (TextView) findViewById(R.id.header_user_name);
                TextView userEmail = (TextView) findViewById(R.id.header_user_email);
                ImageView userImage = (ImageView) findViewById(R.id.header_user_image);

                userName.setText(user.getString("name"));
                userEmail.setText(user.getString("email"));

                // Image
                String url = "http://gravatar.com/avatar/" + Utils.md5Hex(user.getString("email")) + "?s=200";
                ImageLoader.getInstance().displayImage(url, userImage);
            } else {
                GetJson g = new GetJson(this, null, GetJson.WHOAMIURL, PreferenceManager.getDefaultSharedPreferences(this));
                g.execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse date
     *
     * @param dateString
     * @return
     */
    public static Date parseDate(String dateString) {
        Date date = null;
        try {
            // Event date
            DateFormat dbDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", new Locale("nl"));
            if (!dateString.equals("null")) {
                date = dbDF.parse(dateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
