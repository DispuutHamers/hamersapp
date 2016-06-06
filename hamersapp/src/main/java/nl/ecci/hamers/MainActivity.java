package nl.ecci.hamers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.ecci.hamers.beers.BeerFragment;
import nl.ecci.hamers.beers.NewBeerActivity;
import nl.ecci.hamers.events.EventFragment;
import nl.ecci.hamers.events.EventListFragment;
import nl.ecci.hamers.events.NewEventActivity;
import nl.ecci.hamers.gcm.RegistrationIntentService;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.Utils;
import nl.ecci.hamers.meetings.MeetingFragment;
import nl.ecci.hamers.news.NewNewsActivity;
import nl.ecci.hamers.news.NewsFragment;
import nl.ecci.hamers.quotes.NewQuoteFragment;
import nl.ecci.hamers.quotes.QuoteFragment;
import nl.ecci.hamers.users.User;
import nl.ecci.hamers.users.UserFragment;
import nl.ecci.hamers.users.UserListFragment;

public class MainActivity extends AppCompatActivity {
    // Fragments
    public static final QuoteFragment QUOTE_FRAGMENT = new QuoteFragment();
    public static final UserFragment USER_FRAGMENT = new UserFragment();
    public static final UserListFragment USER_FRAGMENT_ALL = new UserListFragment();
    public static final UserListFragment USER_FRAGMENT_EX = new UserListFragment();
    public static final EventFragment EVENT_FRAGMENT = new EventFragment();
    public static final EventListFragment EVENT_FRAGMENT_ALL = new EventListFragment();
    public static final EventListFragment EVENT_FRAGMENT_UPCOMING = new EventListFragment();
    public static final NewsFragment NEWS_FRAGMENT = new NewsFragment();
    public static final BeerFragment BEER_FRAGMENT = new BeerFragment();
    private static final MotionFragment MOTION_FRAGMENT = new MotionFragment();
    public static final MeetingFragment MEETING_FRAGMENT = new MeetingFragment();
    private static final SettingsFragment SETTINGS_FRAGMENT = new SettingsFragment();
    private static final AboutFragment ABOUT_FRAGMENT = new AboutFragment();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final Locale locale = new Locale("nl_NL");
    public static final DateFormat dbDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", locale);
    public static final DateFormat appDF = new SimpleDateFormat("EEE dd MMM yyyy HH:mm", locale);
    public static final DateFormat appDF2 = new SimpleDateFormat("EEEE dd MMMM yyyy", locale);
    public static SharedPreferences prefs;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private boolean backPressedOnce;
    // GCM
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    /**
     * Setup of default ImageLoader configuration (Universal Image Loader)
     * https://github.com/nostra13/Android-Universal-Image-Loader
     */
    private static void configureDefaultImageLoader(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoaderConfiguration defaultConfiguration
                = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .defaultDisplayImageOptions(options)
                .build();

        // Initialize ImageLoader with configuration
        ImageLoader.getInstance().init(defaultConfiguration);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initDrawer();
        initToolbar();

        if (savedInstanceState == null) {
            selectItem(R.id.navigation_item_quotes);
        }

        configureDefaultImageLoader(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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

        Utils.hasApiKey(this, prefs);

        fillHeader();
    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                hideKeyboard();
            }
        });

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
        drawerLayout.addDrawerListener(mDrawerToggle);

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
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.gps_missing), Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
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

    /**
     * Swaps fragments in the quote_menu content view
     */
    private void selectItem(int id) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        hideKeyboard();
        switch (id) {
            case R.id.navigation_item_quotes:
                transaction.replace(R.id.content_frame, QUOTE_FRAGMENT).commit();
                setTitle(getString(R.string.navigation_item_quotes));
                break;

            case R.id.navigation_item_events:
                transaction.replace(R.id.content_frame, EVENT_FRAGMENT).commit();
                setTitle(getString(R.string.navigation_item_events));
                break;

            case R.id.navigation_item_beers:
                transaction.replace(R.id.content_frame, BEER_FRAGMENT).commit();
                setTitle(getString(R.string.navigation_item_beers));
                break;

            case R.id.navigation_item_news:
                transaction.replace(R.id.content_frame, NEWS_FRAGMENT).commit();
                setTitle(getString(R.string.navigation_item_news));
                break;

            case R.id.navigation_item_users:
                transaction.replace(R.id.content_frame, USER_FRAGMENT).commit();
                setTitle(getString(R.string.navigation_item_users));
                break;

            case R.id.navigation_item_motions:
                transaction.replace(R.id.content_frame, MOTION_FRAGMENT).commit();
                setTitle(getString(R.string.navigation_item_motions));
                break;

            case R.id.navigation_item_meetings:
                transaction.replace(R.id.content_frame, MEETING_FRAGMENT).commit();
                setTitle(getString(R.string.navigation_item_meetings));
                break;

            case R.id.navigation_item_settings:
                transaction.replace(R.id.content_frame, SETTINGS_FRAGMENT).commit();
                setTitle(getString(R.string.navigation_item_settings));
                break;

            case R.id.navigation_item_about:
                transaction.replace(R.id.content_frame, ABOUT_FRAGMENT).commit();
                setTitle(getString(R.string.navigation_item_about));
                break;
        }
    }

    /**
     * When user presses "+" in QuoteListFragment, start new dialog with NewQuoteFragment
     */
    public void newQuote(View view) {
        DialogFragment newQuoteFragment = new NewQuoteFragment();
        newQuoteFragment.show(getSupportFragmentManager(), "quotes");
    }

    /**
     * When user presses "+" in EventFragment, start new dialog with NewEventActivity
     */
    public void newEvent(View view) {
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
    }

    /**
     * When user presses "+" in BeerFragment, start new dialog with NewBeerActivity
     */
    public void newBeer(View view) {
        Intent intent = new Intent(this, NewBeerActivity.class);
        startActivity(intent);
    }

    /**
     * When user presses "+" in NewsFragment, start new dialog with NewNewsActivity
     */
    public void newNews(View view) {
        Intent intent = new Intent(this, NewNewsActivity.class);
        startActivity(intent);
    }

    /**
     * When user presses "+" in MeetingFragment, start new dialog with NewMeetingActivity
     */
    public void newMeeting(View view) {
        Snackbar.make(view, getString(R.string.functionality_added_later), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Hides the soft keyboard
     */
    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.backPressedOnce = true;
        Toast.makeText(this, getString(R.string.press_back_again), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, 2000);
    }

    private void fillHeader() {
        User user = DataManager.getOwnUser(prefs);
        if (user.getUserID() != -1) {
            View headerLayout = navigationView.getHeaderView(0);
            TextView userName = (TextView) headerLayout.findViewById(R.id.header_user_name);
            TextView userEmail = (TextView) headerLayout.findViewById(R.id.header_user_email);
            ImageView userImage = (ImageView) headerLayout.findViewById(R.id.header_user_image);

            if (userName != null && userEmail != null && userImage != null) {
                userName.setText(user.getName());
                userEmail.setText(user.getEmail());

                // Image
                String url = DataManager.getGravatarURL(user.getEmail());
                ImageLoader.getInstance().displayImage(url, userImage);
            }
        } else {
            DataManager.getData(this, prefs, DataManager.WHOAMIURL, DataManager.WHOAMIKEY);
        }
    }

    /**
     * Parse date
     */
    public static Date parseDate(String dateString) {
        Date date = null;
        try {
            // Event date
            if (!dateString.equals("null")) {
                date = dbDF.parse(dateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
