package nl.ecci.Hamers.News;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<NewsItem> listItems = new ArrayList<NewsItem>();
    NewsAdapter adapter;
    SwipeRefreshLayout swipeView;
    SharedPreferences prefs;

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

        // Floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.news_add_button);
        fab.attachToRecyclerView(news_list);

        return view;
    }

    public void initSwiper(View view, final RecyclerView news_list, final LinearLayoutManager lm) {
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
        GetJson g = new GetJson(this.getActivity(), this, GetJson.NEWSURL, PreferenceManager.getDefaultSharedPreferences(this.getActivity()), false);
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

                    Date date = parseDate2(temp.getString("date"));

                    NewsItem newsItem = new NewsItem(temp.getString("title").toString(), temp.getString("body").toString(), temp.getString("cat").toString(), date);
                    listItems.add(newsItem);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
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

    public Date parseDate2(String dateString) {
        Date date = null;
        try {
            // News date
            DateFormat dbDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            if (dateString != null) {
                date = dbDF.parse(dateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
