package nl.ecci.hamers.news;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import nl.ecci.hamers.helpers.DataManager;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<News> dataSet = new ArrayList<>();
    private NewsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);
        RecyclerView news_list = (RecyclerView) view.findViewById(R.id.news_recyclerview);

        setHasOptionsMenu(false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        news_list.setLayoutManager(mLayoutManager);

        setHasOptionsMenu(false);

        initSwiper(view, news_list, mLayoutManager);

        adapter = new NewsAdapter(dataSet);
        news_list.setAdapter(adapter);

        onRefresh();

        return view;
    }

    private void initSwiper(View view, final RecyclerView news_list, final LinearLayoutManager lm) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.news_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);

        news_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                swipeRefreshLayout.setEnabled(lm.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        DataManager.getData(getContext(), MainActivity.prefs, DataManager.NEWSURL, DataManager.NEWSKEY);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
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

    @SuppressWarnings("unchecked")
    public void populateList() {
        new populateList().execute(dataSet);
    }

    private class populateList extends AsyncTask<ArrayList<News>, Void, ArrayList<News>> {
        @SafeVarargs
        @Override
        protected final ArrayList<News> doInBackground(ArrayList<News>... param) {
            ArrayList<News> dataSet = new ArrayList<>();
            JSONArray json;
            if ((json = DataManager.getJsonArray(MainActivity.prefs, DataManager.NEWSKEY)) != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                Gson gson = gsonBuilder.create();

                Type type = new TypeToken<ArrayList<News>>() {
                }.getType();
                dataSet = gson.fromJson(json.toString(), type);
            }
            return dataSet;
        }

        @Override
        protected void onPostExecute(ArrayList<News> result) {
            if (!result.isEmpty()) {
                dataSet.clear();
                dataSet.addAll(result);
                if (NewsFragment.this.adapter != null) {
                    NewsFragment.this.adapter.notifyDataSetChanged();
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
