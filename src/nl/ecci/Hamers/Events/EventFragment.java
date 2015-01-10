package nl.ecci.Hamers.Events;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.*;
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
    ArrayAdapter<Event> adapter;
    SwipeRefreshLayout swipeView;
    SharedPreferences prefs;

    public EventFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        ListView event_list = (ListView) view.findViewById(R.id.events_listView);

        setHasOptionsMenu(true);

        initSwiper(view, event_list);

        adapter = new EventsAdapter(this.getActivity(), listItems);
        event_list.setAdapter(adapter);
        event_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                JSONObject e = DataManager.getEvent(prefs, adapter.getItem(position).getTitle(), adapter.getItem(position).getDate());
                if (e != null) {
                    try {
                        Intent intent = new Intent(getActivity(), SingleEventActivity.class);
                        intent.putExtra("title", e.getString("title"));
                        intent.putExtra("beschrijving", e.getString("beschrijving"));
                        intent.putExtra("date", e.getString("date"));

                        ArrayList<String> aanwezig = new ArrayList<String>();
                        ArrayList<String> afwezig = new ArrayList<String>();

                        JSONArray signups = e.getJSONArray("signups");

                        for (int i = 0; i < signups.length(); i++) {
                            JSONObject signup = signups.getJSONObject(i);
                            if (signup.getBoolean("status") == true) {
                                aanwezig.add(DataManager.getUser(prefs, signup.getInt("user_id")).getString("name"));
                            } else {
                                afwezig.add(DataManager.getUser(prefs, signup.getInt("user_id")).getString("name"));
                            }
                        }
                        intent.putStringArrayListExtra("aanwezig", aanwezig);
                        intent.putStringArrayListExtra("afwezig", afwezig);
                        startActivity(intent);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });

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
                if (event_list != null && event_list.getChildCount() > 0) {
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

                        Event tempEvent = new Event(temp.getString("title").toString(), temp.getString("beschrijving").toString(), finalDate, temp.getString("end_time"));
                        listItems.add(tempEvent);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        ;
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
