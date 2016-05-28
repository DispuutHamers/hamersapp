package nl.ecci.hamers.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;

import static nl.ecci.hamers.MainActivity.parseDate;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final ArrayList<NewsItem> listItems = new ArrayList<>();
    private NewsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);
        RecyclerView news_list = (RecyclerView) view.findViewById(R.id.news_recyclerview);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        news_list.setLayoutManager(mLayoutManager);

        initSwiper(view, news_list, mLayoutManager);

        adapter = new NewsAdapter(getActivity(), listItems);
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

    public void populateList() {
        listItems.clear();
        JSONArray json;
        try {
            if ((json = DataManager.getJsonArray(MainActivity.prefs, DataManager.NEWSKEY)) != null) {
                for (int i = json.length() - 1; i >= 0; i--) {
                    JSONObject temp;
                    temp = json.getJSONObject(i);

                    Date date = parseDate(temp.getString("date"));

                    NewsItem newsItem = new NewsItem(temp.getString("title"), temp.getString("body"), temp.getString("cat"), date);
                    listItems.add(newsItem);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.snackbar_downloaderror), Toast.LENGTH_SHORT).show();
        }
        setRefreshing(false);
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
}
