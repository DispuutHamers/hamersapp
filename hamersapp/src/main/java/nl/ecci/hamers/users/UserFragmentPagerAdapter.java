package nl.ecci.hamers.users;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

class UserFragmentPagerAdapter extends FragmentPagerAdapter {
    final static String exUser = "exUser";
    private static String[] tabTitles = null;

    private UserListFragment userFragmentAll;
    private UserListFragment userFragmentEx;

    UserFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        tabTitles = new String[]{
                context.getResources().getString(R.string.menu_users),
                context.getResources().getString(R.string.menu_users_ex)
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
                if (userFragmentAll == null) {
                    userFragmentAll = new UserListFragment();
                    Bundle argAll = new Bundle();
                    argAll.putBoolean(exUser, false);
                    userFragmentAll.setArguments(argAll);
                }
                return userFragmentAll;
            case 1:
                if (userFragmentEx == null) {
                    userFragmentEx = new UserListFragment();
                    Bundle argEx = new Bundle();
                    argEx.putBoolean(exUser, true);
                    userFragmentEx.setArguments(argEx);
                }
                return userFragmentEx;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
