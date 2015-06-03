package nl.ecci.Hamers.Beers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.melnykov.fab.FloatingActionButton;
import nl.ecci.Hamers.Helpers.AnimateFirstDisplayListener;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.DividerItemDecoration;
import nl.ecci.Hamers.Helpers.GetJson;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BeerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static RelativeLayout parentLayout;
    private final ArrayList<Beer> listItems = new ArrayList<>();
    private BeerAdapter adapter;
    private SwipeRefreshLayout swipeView;
    private SharedPreferences prefs;

    public BeerFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_fragment, container, false);
        RecyclerView beer_list = (RecyclerView) view.findViewById(R.id.beer_recyclerview);

        parentLayout = (RelativeLayout) view.findViewById(R.id.beer_parent);

        setHasOptionsMenu(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if (prefs.getString("beerData", null) != null) {
            populateList(prefs);
        } else {
            onRefresh();
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        beer_list.setLayoutManager(mLayoutManager);
        beer_list.setItemAnimator(new DefaultItemAnimator());
        beer_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        initSwiper(view, beer_list, mLayoutManager);

        adapter = new BeerAdapter(listItems, getActivity(), parentLayout);
        beer_list.setAdapter(adapter);

        sort();

        // Floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.beer_add_button);
        fab.attachToRecyclerView(beer_list);

        return view;
    }

    private void initSwiper(View view, final RecyclerView beer_list, final LinearLayoutManager lm) {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.beer_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(this);

        beer_list.setOnScrollListener(new RecyclerView.OnScrollListener() {

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
                swipeView.setEnabled(enable);
            }
        });
    }

    @Override
    public void onRefresh() {
        GetJson g = new GetJson(this.getActivity(), this, GetJson.BEERURL, PreferenceManager.getDefaultSharedPreferences(this.getActivity()));
        g.execute();
    }

    public void populateList(SharedPreferences prefs) {
        this.prefs = prefs;
        listItems.clear();
        JSONArray json;
        try {
            if ((json = DataManager.getJsonArray(prefs, DataManager.BEERKEY)) != null) {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject temp;
                    temp = json.getJSONObject(i);
                    Beer tempBeer;

                    String cijfer = temp.getString("cijfer");
                    if (cijfer.equals("null")) {
                        tempBeer = new Beer(temp.getInt("id"), temp.getString("name"), temp.getString("soort"),
                                temp.getString("picture"), temp.getString("percentage"), temp.getString("brewer"), temp.getString("country"), "nog niet bekend");
                    } else {
                        tempBeer = new Beer(temp.getInt("id"), temp.getString("name"), temp.getString("soort"),
                                temp.getString("picture"), temp.getString("percentage"), temp.getString("brewer"), temp.getString("country"), cijfer);
                    }
                    listItems.add(tempBeer);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } catch (JSONException e) {
            if (this.getActivity() != null) {
                Toast.makeText(getActivity(), getString(R.string.snackbar_downloaderror), Toast.LENGTH_SHORT).show();
            }
        }
        if (swipeView != null) {
            swipeView.setRefreshing(false);
            sort();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.beer_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_name:
                sortByName();
                return true;
            case R.id.sort_rating:
                sortByRating();
                return true;
            default:
                return false;
        }
    }

    private void sort() {
        if (getActivity() != null)
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (prefs != null) {
            String sortPref = prefs.getString("beerSort", "");
            if (sortPref.equals("name")) {
                sortByName();
            } else if (sortPref.equals("rating")) {
                sortByRating();
            }
        }
    }

    private void sortByName() {
        Collections.sort(listItems, nameComparator);
        adapter.notifyDataSetChanged();
    }

    private void sortByRating() {
        Collections.sort(listItems, ratingComparator);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AnimateFirstDisplayListener.displayedImages.clear();
    }

    private static final Comparator<Beer> nameComparator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            String name1 = beer1.getName();
            String name2 = beer2.getName();

            return name1.compareToIgnoreCase(name2);
        }
    };
    private static final Comparator<Beer> ratingComparator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            String rating1 = beer1.getRating();
            String rating2 = beer2.getRating();

            if (rating1.equals("nog niet bekend")) {
                rating1 = "-1";
            } else if (rating2.equals("nog niet bekend")) {
                rating2 = "-1";
            }
            return rating2.compareToIgnoreCase(rating1);
        }
    };
}