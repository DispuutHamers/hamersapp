package nl.ecci.hamers.users;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

class UserFragmentPagerAdapter extends FragmentPagerAdapter {
    public final static String exUser = "exUser";
    private static String[] tabTitles = null;
    private final int PAGE_COUNT = 2;

    public UserFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        tabTitles = new String[]{
                context.getResources().getString(R.string.menu_users),
                context.getResources().getString(R.string.menu_users_ex)
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
                if (MainActivity.USER_FRAGMENT_ALL.getArguments() == null) {
                    Bundle argAll = new Bundle();
                    argAll.putBoolean(exUser, false);
                    MainActivity.USER_FRAGMENT_ALL.setArguments(argAll);
                }
                return MainActivity.USER_FRAGMENT_ALL;
            case 1:
                if (MainActivity.USER_FRAGMENT_EX.getArguments() == null) {
                    Bundle argEx = new Bundle();
                    argEx.putBoolean(exUser, true);
                    MainActivity.USER_FRAGMENT_EX.setArguments(argEx);
                }
                return MainActivity.USER_FRAGMENT_EX;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
