package nl.ecci.Hamers.Events;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.Toast;
import com.melnykov.fab.FloatingActionButton;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.GetJson;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import static nl.ecci.Hamers.MainActivity.parseDate;

public class EventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<Event> listItems = new ArrayList<Event>();
    EventsAdapter adapter;
    SwipeRefreshLayout swipeView;
    SharedPreferences prefs;

    public EventFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        RecyclerView event_list = (RecyclerView) view.findViewById(R.id.events_recyclerview);

        //setHasOptionsMenu(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        event_list.setLayoutManager(mLayoutManager);

        initSwiper(view, event_list, mLayoutManager);

        adapter = new EventsAdapter(listItems, getActivity());
        event_list.setAdapter(adapter);

        // Floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.event_add_button);
        fab.attachToRecyclerView(event_list);

        return view;
    }

    public void initSwiper(View view, final RecyclerView event_list, final LinearLayoutManager lm) {
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
        GetJson g = new GetJson(this.getActivity(), this, GetJson.EVENTURL, PreferenceManager.getDefaultSharedPreferences(this.getActivity()), false);
        g.execute();
    }

    public void populateList(SharedPreferences prefs) {
        this.prefs = prefs;
        listItems.clear();
        JSONArray json;
        try {
            if ((json = DataManager.getJsonArray(prefs, DataManager.EVENTKEY)) != null) {
                for (int i = json.length() - 1; i >= 0; i--) {
                    JSONObject temp;
                    try {
                        temp = json.getJSONObject(i);
                        String finalDate = parseDate(temp.getString("date").substring(0, 10));
                        Event tempEvent = new Event(temp.getInt("id"), temp.getString("title").toString(), temp.getString("beschrijving").toString(), temp.getString("location").toString(), finalDate, temp.getString("end_time"));
                        listItems.add(tempEvent);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
        }
        if (swipeView != null) {
            swipeView.setRefreshing(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_list_menu, menu);
    }
}
