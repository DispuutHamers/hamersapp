package nl.ecci.hamers.users;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.ecci.hamers.R;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.user_fragment, container, false);

        UserFragmentPagerAdapter adapter = new UserFragmentPagerAdapter(getActivity(), getChildFragmentManager());
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.user_fragment_viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.user_fragment_sliding_tabs);
        tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.sliding_tabs_text_normal), ContextCompat.getColor(getContext(), R.color.sliding_tabs_text_selected));
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityCompat.invalidateOptionsMenu(getActivity());
    }
}
