package nl.ecci.hamers.events;

import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;

import static nl.ecci.hamers.MainActivity.parseDate;

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
        if (upcoming) {
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
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

    @Override
    public void onRefresh() {
        DataManager.getData(getContext(), MainActivity.prefs, DataManager.EVENTURL, DataManager.EVENTKEY);
        DataManager.getData(getContext(), MainActivity.prefs, DataManager.SIGNUPURL, DataManager.SIGNUPKEY);
    }

    public void populateList() {
        new populateList().execute("");
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
        if (upcoming) {
            event_list.smoothScrollToPosition(adapter.getItemCount() - 1);
        } else {
            event_list.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
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

    public class populateList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            dataSet.clear();
            JSONArray json;
            Date currentDate = Calendar.getInstance().getTime();
            try {
                if ((json = DataManager.getJsonArray(MainActivity.prefs, DataManager.EVENTKEY)) != null) {
                    for (int i = json.length() - 1; i >= 0; i--) {
                        JSONObject temp;
                        temp = json.getJSONObject(i);

                        Date date = parseDate(temp.getString("date"));
                        Date end_time = parseDate(temp.getString("end_time"));
                        Date deadline = parseDate(temp.getString("deadline"));

                        Event event = new Event(temp.getInt("id"), temp.getString("title"), temp.getString("beschrijving"), temp.getString("location"), date, end_time, deadline, temp.getJSONArray("signups"));

                        if (upcoming) {
                            if (date.after(currentDate)) {
                                dataSet.add(event);
                            }
                        } else {
                            dataSet.add(event);
                        }
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), getString(R.string.snackbar_loaderror), Toast.LENGTH_SHORT).show();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (EventListFragment.this.adapter != null) {
                EventListFragment.this.adapter.notifyDataSetChanged();
            }
            setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}

