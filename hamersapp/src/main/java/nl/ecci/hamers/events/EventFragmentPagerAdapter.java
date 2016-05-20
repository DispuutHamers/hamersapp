package nl.ecci.hamers.events;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

public class EventFragmentPagerAdapter extends FragmentPagerAdapter {
    private static String[] tabTitles = null;
    private final int PAGE_COUNT = 2;
    public final static String upcoming = "upcoming";

    public EventFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        tabTitles = new String[]{
                context.getResources().getString(R.string.menu_upcoming),
                context.getResources().getString(R.string.menu_all)
        };
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (MainActivity.EVENT_FRAGMENT_UPCOMING.getArguments() == null) {
                    Bundle argUpcoming = new Bundle();
                    argUpcoming.putBoolean(upcoming, true);
                    MainActivity.EVENT_FRAGMENT_UPCOMING.setArguments(argUpcoming);
                }
                return MainActivity.EVENT_FRAGMENT_UPCOMING;
            case 1:
                if (MainActivity.EVENT_FRAGMENT_ALL.getArguments() == null) {
                    Bundle argAll = new Bundle();
                    argAll.putBoolean(upcoming, false);
                    MainActivity.EVENT_FRAGMENT_ALL.setArguments(argAll);
                }
                return MainActivity.EVENT_FRAGMENT_ALL;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
