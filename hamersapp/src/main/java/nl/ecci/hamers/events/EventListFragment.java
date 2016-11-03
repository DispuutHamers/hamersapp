package nl.ecci.hamers.events;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.VolleyCallback;

public class EventListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final ArrayList<Event> dataSet = new ArrayList<>();
    private EventListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView event_list;
    private boolean upcoming;

    public EventListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_list_fragment, container, false);
        event_list = (RecyclerView) view.findViewById(R.id.events_recyclerview);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        event_list.setLayoutManager(mLayoutManager);

        setHasOptionsMenu(true);

        initSwiper(view, event_list, mLayoutManager);

        adapter = new EventListAdapter(getActivity(), dataSet);
        event_list.setAdapter(adapter);

        upcoming = getArguments().getBoolean(EventFragmentPagerAdapter.upcoming, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.event_create_button);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), NewEventActivity.class);
                    startActivity(intent);
                }
            });
        }

        onRefresh();

        return view;
    }

    private void initSwiper(View view, final RecyclerView event_list, final LinearLayoutManager lm) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.events_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);

        event_list.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                swipeRefreshLayout.setEnabled(lm.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRefresh() {
        setRefreshing(true);
        if (upcoming) {
            DataManager.getData(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    new populateList().execute(dataSet);
                }
                @Override
                public void onError(VolleyError error) {
                    // Nothing
                }
            }, getContext(), MainActivity.prefs, DataManager.EVENTURL);
        } else {
            DataManager.getData(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    new populateList().execute(dataSet);
                }
                @Override
                public void onError(VolleyError error) {
                    // Nothing
                }
            }, getContext(), MainActivity.prefs, DataManager.UPCOMINGEVENTURL);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scroll_top:
                event_list.smoothScrollToPosition(0);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_event_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.event_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.search_hint));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapter.getFilter().filter(s.toLowerCase());
                    return false;
                }
            });
        }
    }

    private void setRefreshing(final Boolean bool) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(bool);
                }
            });
        }
    }

    private class populateList extends AsyncTask<ArrayList<Event>, Void, ArrayList<Event>> {
        @SafeVarargs
        @Override
        protected final ArrayList<Event> doInBackground(ArrayList<Event>... param) {
            ArrayList<Event> dataSet = new ArrayList<>();
            JSONArray json;

            Date now = new Date();

            String key = DataManager.EVENTKEY;
            if (upcoming) {
                key = DataManager.UPCOMINGEVENTKEY;
            }

            if ((json = DataManager.getJsonArray(MainActivity.prefs, key)) != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                Gson gson = gsonBuilder.create();

                try {
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject temp = json.getJSONObject(i);
                        Event event = gson.fromJson(temp.toString(), Event.class);
                        if (upcoming) {
                            if (event.getEndDate().after(now)) {
                                dataSet.add(event);
                            }
                        } else {
                            dataSet.add(event);
                        }
                    }
                } catch (JSONException ignored) {
                }
            }
            return dataSet;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> result) {
            if (!result.isEmpty()) {
                dataSet.clear();
                dataSet.addAll(result);
                Collections.reverse(dataSet);
                if (EventListFragment.this.adapter != null) {
                    EventListFragment.this.adapter.notifyDataSetChanged();
                }
            }
            setRefreshing(false);
        }
    }
}

