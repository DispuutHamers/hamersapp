package nl.ecci.hamers.events;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nl.ecci.hamers.R;

class EventFragmentPagerAdapter extends FragmentPagerAdapter {
    final static String upcoming = "upcoming";
    private static String[] tabTitles = null;

    private EventListFragment eventFragmentUpcoming;
    private EventListFragment eventFragmentAll;

    EventFragmentPagerAdapter(Context context, FragmentManager fm) {
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
                if (eventFragmentUpcoming == null) {
                    eventFragmentUpcoming = new EventListFragment();
                    Bundle argUpcoming = new Bundle();
                    argUpcoming.putBoolean(upcoming, true);
                    eventFragmentUpcoming.setArguments(argUpcoming);
                }
                return eventFragmentUpcoming;
            case 1:
                if (eventFragmentAll == null) {
                    eventFragmentAll = new EventListFragment();
                    Bundle argAll = new Bundle();
                    argAll.putBoolean(upcoming, false);
                    eventFragmentAll.setArguments(argAll);
                }
                return eventFragmentAll;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
