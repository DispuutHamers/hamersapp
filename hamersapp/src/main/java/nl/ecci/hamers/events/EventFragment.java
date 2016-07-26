package nl.ecci.hamers.events;

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

public class EventFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.event_fragment, container, false);

        EventFragmentPagerAdapter adapter = new EventFragmentPagerAdapter(getActivity(), getChildFragmentManager());
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.event_fragment_viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.event_fragment_sliding_tabs);
        tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.sliding_tabs_text_normal), ContextCompat.getColor(getContext(), R.color.sliding_tabs_text_selected));
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityCompat.invalidateOptionsMenu(getActivity());
    }
}
