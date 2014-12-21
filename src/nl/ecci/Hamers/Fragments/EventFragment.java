package nl.ecci.Hamers.Fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import nl.ecci.Hamers.Adapters.EventsAdapter;
import nl.ecci.Hamers.Event;
import nl.ecci.Hamers.GetJson;
import nl.ecci.Hamers.MainActivity;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;
import static nl.ecci.Hamers.MainActivity.parseDate;

public class EventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public EventFragment() {
        // Empty constructor required for fragment subclasses
    }

    ArrayList<Event> listItems = new ArrayList<Event>();
    ArrayAdapter<Event> adapter;
    SwipeRefreshLayout swipeView;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        ListView event_list = (ListView) view.findViewById(R.id.events_listView);

        initSwiper(view, event_list);

        adapter = new EventsAdapter(this.getActivity(), listItems);
        event_list.setAdapter(adapter);

        return view;
    }

    public void initSwiper(View view, final ListView event_list) {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.events_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(this);

        event_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(event_list != null && event_list.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = event_list.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = event_list.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeView.setEnabled(enable);
            }
        });
    }

    @Override
    public void onRefresh() {
        GetJson g = new GetJson(this.getActivity(), this, GetJson.EVENT, PreferenceManager.getDefaultSharedPreferences(this.getActivity()), false);
        g.execute();
    }

    public void populateList(SharedPreferences prefs) {
        listItems.clear();
        JSONArray json;
        try {
            if ((json = new JSONArray(prefs.getString("eventData", null))) != null) {
                for (int i = json.length()-1; i >= 0; i--) {
                    JSONObject temp;
                    try {
                        temp = json.getJSONObject(i);

                        String finalDate = parseDate(temp.getString("date").substring(0, 10));

                        Event tempEvent = new Event(temp.getString("title").toString(), temp.getString("beschrijving").toString(), finalDate, temp.getString("end_time"));
                        listItems.add(tempEvent);
                        if(adapter != null){adapter.notifyDataSetChanged();};
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
        }
        if(swipeView != null) {swipeView.setRefreshing(false);}
    }
}