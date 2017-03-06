package nl.ecci.hamers.meetings;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.HamersFragment;
import nl.ecci.hamers.loader.GetCallback;
import nl.ecci.hamers.loader.Loader;

import static nl.ecci.hamers.MainActivity.prefs;

public class MeetingFragment extends HamersFragment {
    private final ArrayList<Meeting> dataSet = new ArrayList<>();
    private MeetingAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hamers_fragment, container, false);
        RecyclerView meetingList = (RecyclerView) view.findViewById(R.id.hamers_recyclerview);

        setHasOptionsMenu(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        meetingList.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.hamers_swipe_container);
        initSwiper(meetingList, layoutManager, swipeRefreshLayout);

        adapter = new MeetingAdapter(dataSet, getActivity());
        meetingList.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.hamers_fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(getActivity(), NewMeetingActivity.class), 1);
                }
            });
        }

        onRefresh();

        return view;
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        Loader.getData(getContext(), Loader.MEETINGURL, new GetCallback() {
            @Override
            public void onSuccess(String response) {
                new populateList().execute(response);
            }

            @Override
            public void onError(VolleyError error) {
                // Nothing
            }
        }, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
        getActivity().setTitle(getResources().getString(R.string.navigation_item_meetings));
    }

    private class populateList extends AsyncTask<String, Void, ArrayList<Meeting>> {
        @Override
        protected final ArrayList<Meeting> doInBackground(String... params) {
            ArrayList<Meeting> result;
            Type type = new TypeToken<ArrayList<Meeting>>() {
            }.getType();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern());
            Gson gson = gsonBuilder.create();

            if (params.length > 0) {
                result = gson.fromJson(params[0], type);
            } else {
                result = gson.fromJson(prefs.getString(Loader.MEETINGURL, null), type);
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Meeting> result) {
            if (!result.isEmpty()) {
                dataSet.clear();
                dataSet.addAll(result);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
            setRefreshing(false);
        }
    }
}
