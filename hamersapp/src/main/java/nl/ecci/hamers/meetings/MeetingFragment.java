package nl.ecci.hamers.meetings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.beers.Beer;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.DividerItemDecoration;

public class MeetingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final ArrayList<Meeting> dataSet = new ArrayList<>();
    private MeetingAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meeting_fragment, container, false);
        RecyclerView meeting_list = (RecyclerView) view.findViewById(R.id.meeting_recyclerview);

        setHasOptionsMenu(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        meeting_list.setLayoutManager(mLayoutManager);
        meeting_list.setItemAnimator(new DefaultItemAnimator());
        meeting_list.addItemDecoration(new DividerItemDecoration(getActivity()));

        initSwiper(view, meeting_list, mLayoutManager);

        adapter = new MeetingAdapter(dataSet, getActivity());
        meeting_list.setAdapter(adapter);

        onRefresh();

        return view;
    }

    private void initSwiper(View view, final RecyclerView beer_list, final LinearLayoutManager lm) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.beer_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(this);

        beer_list.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                boolean enable = false;
                if (beer_list != null && beer_list.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lm.findFirstCompletelyVisibleItemPosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = beer_list.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
    }

    @Override
    public void onRefresh() {
        DataManager.getData(getContext(), MainActivity.prefs, DataManager.MEETINGURL, DataManager.MEETINGKEY);
    }

    @SuppressWarnings("unchecked")
    public void populateList() {
        new populateList().execute(dataSet);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
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

    public class populateList extends AsyncTask<ArrayList<Meeting>, Void, ArrayList<Meeting>> {
        @SafeVarargs
        @Override
        protected final ArrayList<Meeting> doInBackground(ArrayList<Meeting>... param) {
            ArrayList<Meeting> dataSet = new ArrayList<>();
            JSONArray json;
            if ((json = DataManager.getJsonArray(MainActivity.prefs, DataManager.MEETINGKEY)) != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                Gson gson = gsonBuilder.create();

                Type type = new TypeToken<ArrayList<Beer>>() {
                }.getType();
                dataSet = gson.fromJson(json.toString(), type);
            }
            return dataSet;
        }

        @Override
        protected void onPostExecute(ArrayList<Meeting> result) {
            if (!result.isEmpty()) {
                dataSet.clear();
                dataSet.addAll(result);
                if (MeetingFragment.this.adapter != null) {
                    MeetingFragment.this.adapter.notifyDataSetChanged();
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
