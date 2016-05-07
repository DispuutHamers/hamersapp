package nl.ecci.hamers.beers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.DividerItemDecoration;

import static nl.ecci.hamers.MainActivity.parseDate;

public class BeerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final ArrayList<Beer> dataSet = new ArrayList<>();
    private BeerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences prefs;
    private RecyclerView beer_list;

    public BeerFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_fragment, container, false);
        beer_list = (RecyclerView) view.findViewById(R.id.beer_recyclerview);

        setHasOptionsMenu(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        onRefresh();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        beer_list.setLayoutManager(mLayoutManager);
        beer_list.setItemAnimator(new DefaultItemAnimator());
        beer_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        initSwiper(view, beer_list, mLayoutManager);

        adapter = new BeerAdapter(dataSet, getActivity());
        beer_list.setAdapter(adapter);

        sortList();

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
        // Get beers
        DataManager.getData(getContext(), prefs, DataManager.BEERURL, DataManager.BEERKEY);
        // Get reviews
        DataManager.getData(getContext(), prefs, DataManager.REVIEWURL, DataManager.REVIEWKEY);
    }

    public void populateList(SharedPreferences prefs) {
        this.prefs = prefs;
        dataSet.clear();
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
                                temp.getString("picture"), temp.getString("percentage"), temp.getString("brewer"), temp.getString("country"), "nog niet bekend", parseDate(temp.getString("created_at")));
                    } else {
                        tempBeer = new Beer(temp.getInt("id"), temp.getString("name"), temp.getString("soort"),
                                temp.getString("picture"), temp.getString("percentage"), temp.getString("brewer"), temp.getString("country"), cijfer, parseDate(temp.getString("created_at")));
                    }
                    dataSet.add(tempBeer);
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
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
            sortList();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.beer_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.beer_search);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scroll_top:
                scrollTop();
                return true;
            case R.id.sort_name:
                sort(nameComparator);
                return true;
            case R.id.sort_rating:
                sort(ratingComparator);
                return true;
            case R.id.sort_date_asc:
                sort(dateASCComperator);
                return true;
            case R.id.sort_date_desc:
                sort(dateDESCComperator);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList(prefs);
    }

    private void scrollTop() {
        beer_list.smoothScrollToPosition(0);
    }

    private void sortList() {
        if (getActivity() != null)
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (prefs != null) {
            String sortPref = prefs.getString("beerSort", "");
            switch (sortPref) {
                case "name":
                    sort(nameComparator);
                    break;
                case "rating":
                    sort(ratingComparator);
                    break;
                case "datumASC":
                    sort(dateASCComperator);
                    break;
                case "datumDESC":
                    sort(dateDESCComperator);
                    break;
            }
        }
    }

    private void sort(Comparator<Beer> comperator) {
        Collections.sort(dataSet, comperator);
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

    private static final Comparator<Beer> dateASCComperator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            Date date1 = beer1.getCreatedAt();
            Date date2 = beer2.getCreatedAt();

            return date1.compareTo(date2);
        }
    };

    private static final Comparator<Beer> dateDESCComperator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            Date date1 = beer1.getCreatedAt();
            Date date2 = beer2.getCreatedAt();

            return date2.compareTo(date1);
        }
    };
}