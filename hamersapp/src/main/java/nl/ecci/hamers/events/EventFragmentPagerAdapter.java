package nl.ecci.hamers.events;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

class EventFragmentPagerAdapter extends FragmentPagerAdapter {
    public final static String upcoming = "upcoming";
    private static String[] tabTitles = null;

    public EventFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        tabTitles = new String[]{
                context.getResources().getString(R.string.menu_upcoming),
                context.getResources().getString(R.string.menu_all)
        };
    }

    @Override
    public int getCount() {
        return 2;
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
        return tabTitles[position];
    }
}
