package nl.ecci.Hamers.Beers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.*;
import nl.ecci.Hamers.Helpers.GetJson;
import nl.ecci.Hamers.Helpers.JSONHelper;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.AdapterView.OnItemClickListener;

public class BeerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<Beer> listItems = new ArrayList<Beer>();
    ArrayAdapter<Beer> adapter;
    SwipeRefreshLayout swipeView;
    SharedPreferences prefs;

    public BeerFragment() {
        // Empty constructor required for fragment subclasses
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_fragment, container, false);
        ListView beer_list = (ListView) view.findViewById(R.id.beer_listView);

        setHasOptionsMenu(true);

        initSwiper(view, beer_list);

        adapter = new BeersAdapter(this.getActivity(), listItems);
        beer_list.setAdapter(adapter);
        beer_list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                JSONObject b = JSONHelper.getBeer(prefs, adapter.getItem(position).getName());
                if (b != null) {
                    try {
                        System.out.println(b.getString("name"));
                        Intent intent = new Intent(getActivity(), SingleBeerActivity.class);
                        intent.putExtra("name", b.getString("name"));
                        intent.putExtra("soort", b.getString("soort"));
                        intent.putExtra("percentage", b.getString("percentage"));
                        intent.putExtra("brewer", b.getString("brewer"));
                        intent.putExtra("country", b.getString("country"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

    public void initSwiper(View view, final ListView beer_list) {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.beer_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(this);

        beer_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (beer_list != null && beer_list.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = beer_list.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = beer_list.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeView.setEnabled(enable);
            }
        });
    }

    @Override
    public void onRefresh() {
        GetJson g = new GetJson(this.getActivity(), this, GetJson.BEERURL, PreferenceManager.getDefaultSharedPreferences(this.getActivity()), false);
        g.execute();
    }

    public void populateList(SharedPreferences prefs) {
        this.prefs = prefs;
        listItems.clear();
        JSONArray json;
        try {
            if ((json = JSONHelper.getJsonArray(prefs, JSONHelper.BEERKEY)) != null) {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject temp;
                    temp = json.getJSONObject(i);
                    Beer tempBeer = new Beer(temp.getString("name").toString(), temp.getString("soort").toString(),
                            temp.getString("picture"), temp.getString("percentage"), temp.getString("brewer"), temp.getString("country"));
                    listItems.add(tempBeer);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    ;
                }
            }
        } catch (JSONException e) {
            if (this.getActivity() != null) {
                Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
            }
        }
        if (swipeView != null) {
            swipeView.setRefreshing(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.beer_list_menu, menu);
    }
}