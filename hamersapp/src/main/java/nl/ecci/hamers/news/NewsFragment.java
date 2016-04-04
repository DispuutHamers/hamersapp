package nl.ecci.hamers.news;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.GetJson;

import static nl.ecci.hamers.MainActivity.parseDate;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final ArrayList<NewsItem> listItems = new ArrayList<>();
    private NewsAdapter adapter;
    private SwipeRefreshLayout swipeView;
    private SharedPreferences prefs;
    public static RelativeLayout parentLayout;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);
        RecyclerView news_list = (RecyclerView) view.findViewById(R.id.news_recyclerview);

        parentLayout = (RelativeLayout) view.findViewById(R.id.news_parent);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        news_list.setLayoutManager(mLayoutManager);

        initSwiper(view, news_list, mLayoutManager);

        adapter = new NewsAdapter(getActivity(), listItems);
        news_list.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if (prefs.getString("newsData", null) != null) {
            populateList(prefs);
        } else {
            onRefresh();
        }

        // Floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.news_add_button);
        fab.attachToRecyclerView(news_list);

        return view;
    }

    private void initSwiper(View view, final RecyclerView news_list, final LinearLayoutManager lm) {
        // SwipeRefreshLayout
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.news_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        news_list.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                swipeView.setEnabled(lm.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeView.setEnabled(true);
        GetJson g = new GetJson(this.getActivity(), this, GetJson.NEWSURL, PreferenceManager.getDefaultSharedPreferences(this.getActivity()));
        g.execute();
    }

    public void populateList(SharedPreferences prefs) {
        this.prefs = prefs;
        listItems.clear();
        JSONArray json;
        try {
            if ((json = DataManager.getJsonArray(prefs, DataManager.NEWSKEY)) != null) {
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
        if (swipeView != null) {
            swipeView.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList(prefs);
    }
}
