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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.beers.Beer;
import nl.ecci.hamers.helpers.DataManager;

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

        // If upcoming, reverse order
        upcoming = getArguments().getBoolean(EventFragmentPagerAdapter.upcoming, false);
//        if (upcoming) {
//            mLayoutManager.setReverseLayout(true);
//            mLayoutManager.setStackFromEnd(true);
//        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.event_create_button);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createEvent(null);
                }
            });
        }

        onRefresh();

        return view;
    }

    private void initSwiper(View view, final RecyclerView event_list, final LinearLayoutManager lm) {
        // SwipeRefreshLayout
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

    public void createEvent(Event event) {
        Intent intent = new Intent(getActivity(), NewEventActivity.class);

        if (event != null) {
            intent.putExtra(Event.EVENT, event.getID());
        }

        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if (upcoming) {
            DataManager.getData(getContext(), MainActivity.prefs, DataManager.EVENTURL, DataManager.EVENTKEY);
        } else {
            DataManager.getData(getContext(), MainActivity.prefs, DataManager.UPCOMINGEVENTURL, DataManager.UPCOMINGEVENTKEY);
        }
    }

    @SuppressWarnings("unchecked")
    public void populateList() {
        new populateList().execute(dataSet);
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
        populateList();
    }

    private void scrollTop() {
//        if (upcoming) {
//            event_list.smoothScrollToPosition(adapter.getItemCount() - 1);
//        } else {
            event_list.smoothScrollToPosition(0);
//        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
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
                        dataSet.add(event);
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
                if (EventListFragment.this.adapter != null) {
                    EventListFragment.this.adapter.notifyDataSetChanged();
                }
            }
            setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }
    }
}

