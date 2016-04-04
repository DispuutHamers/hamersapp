package nl.ecci.hamers.events;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;

import static nl.ecci.hamers.MainActivity.parseDate;

public class EventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static RelativeLayout parentLayout;
    private final ArrayList<Event> listItems = new ArrayList<>();
    private EventAdapter adapter;
    private SwipeRefreshLayout swipeView;
    private SharedPreferences prefs;
    private RecyclerView event_list;

    public EventFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_fragment, container, false);
        event_list = (RecyclerView) view.findViewById(R.id.events_recyclerview);

        parentLayout = (RelativeLayout) view.findViewById(R.id.event_parent);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        event_list.setLayoutManager(mLayoutManager);

        setHasOptionsMenu(true);

        initSwiper(view, event_list, mLayoutManager);

        adapter = new EventAdapter(getActivity(), listItems);
        event_list.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        if (prefs.getString("eventData", null) != null) {
            populateList(prefs);
        } else {
            onRefresh();
        }

        // Floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.event_add_button);
        fab.attachToRecyclerView(event_list);

        return view;
    }

    private void initSwiper(View view, final RecyclerView event_list, final LinearLayoutManager lm) {
        // SwipeRefreshLayout
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.events_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        event_list.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                swipeView.setEnabled(lm.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeView.setEnabled(true);
        DataManager.getData(getContext(), prefs, DataManager.EVENTURL, DataManager.EVENTKEY);
    }

    public void populateList(SharedPreferences prefs) {
        this.prefs = prefs;
        listItems.clear();
        JSONArray json;
        try {
            if ((json = DataManager.getJsonArray(prefs, DataManager.EVENTKEY)) != null) {
                for (int i = json.length() - 1; i >= 0; i--) {
                    JSONObject temp;
                    temp = json.getJSONObject(i);

                    Date date = parseDate(temp.getString("date"));
                    Date end_time = parseDate(temp.getString("end_time"));

                    Event event = new Event(temp.getInt("id"), temp.getString("title"), temp.getString("beschrijving"), temp.getString("location"), date, end_time, temp.getJSONArray("signups"));
                    listItems.add(event);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } catch (JSONException e) {
            Snackbar.make(parentLayout, getString(R.string.snackbar_downloaderror), Snackbar.LENGTH_SHORT).show();
        }
        if (swipeView != null) {
            swipeView.setRefreshing(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scroll_top:
                scrollTop();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList(prefs);
    }

    private void scrollTop() {
        event_list.smoothScrollToPosition(0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
    }
}
