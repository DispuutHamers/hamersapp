package nl.ecci.Hamers.Beers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.*;
import com.software.shell.fab.ActionButton;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.GetJson;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.widget.AdapterView.OnItemClickListener;

public class BeerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<Beer> listItems = new ArrayList<Beer>();
    ArrayAdapter<Beer> adapter;
    SwipeRefreshLayout swipeView;
    SharedPreferences prefs;
    int lastVisibleItem;

    public BeerFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_fragment, container, false);
        ListView beer_list = (ListView) view.findViewById(R.id.beer_listView);
        final ActionButton fab = (ActionButton) view.findViewById(R.id.beer_add_button);
        fab.setShowAnimation(ActionButton.Animations.FADE_IN);
        fab.setHideAnimation(ActionButton.Animations.FADE_OUT);

        setHasOptionsMenu(true);

        initSwiper(view, beer_list, fab);

        adapter = new BeersAdapter(this.getActivity(), listItems);
        beer_list.setAdapter(adapter);
        beer_list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                JSONObject b = DataManager.getBeer(prefs, adapter.getItem(position).getName());
                if (b != null) {
                    try {
                        Intent intent = new Intent(getActivity(), SingleBeerActivity.class);
                        intent.putExtra("id", b.getInt("id"));
                        intent.putExtra("name", b.getString("name"));
                        intent.putExtra("soort", b.getString("soort"));
                        intent.putExtra("percentage", b.getString("percentage"));
                        intent.putExtra("brewer", b.getString("brewer"));
                        intent.putExtra("country", b.getString("country"));
                        intent.putExtra("cijfer", b.getString("cijfer"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sort();

        return view;
    }

    public void initSwiper(View view, final ListView beer_list, final ActionButton fab) {
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


                    // Hide/show add-button (after 0.1 second)
                    if (firstVisibleItem > lastVisibleItem) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {fab.hide();}
                        }, 125);
                    } else if( firstVisibleItem < lastVisibleItem) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {fab.show();}
                        }, 125);
                    }
                    lastVisibleItem = firstVisibleItem;
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
            if ((json = DataManager.getJsonArray(prefs, DataManager.BEERKEY)) != null) {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject temp;
                    temp = json.getJSONObject(i);
                    Beer tempBeer = null;

                    String cijfer = temp.getString("cijfer");
                    if (cijfer.equals("null")) {
                        tempBeer = new Beer(temp.getInt("id"), temp.getString("name").toString(), temp.getString("soort").toString(),
                                temp.getString("picture"), temp.getString("percentage"), temp.getString("brewer"), temp.getString("country"), "nog niet bekend");
                    } else {
                        tempBeer = new Beer(temp.getInt("id"), temp.getString("name").toString(), temp.getString("soort").toString(),
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
                Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
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

    public void sort() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String sortPref = prefs.getString("beerSort", "");
        if (sortPref.equals("name")) {
            sortByName();
        } else if (sortPref.equals("rating")) {
            sortByRating();
        }
    }

    public void sortByName() {
        Collections.sort(listItems, nameComparator);
        adapter.notifyDataSetChanged();
    }

    public void sortByRating() {
        Collections.sort(listItems, ratingComparator);
        adapter.notifyDataSetChanged();
    }

    public Comparator<Beer> nameComparator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {

            String name1 = beer1.getName();
            String name2 = beer2.getName();

            return name1.compareToIgnoreCase(name2);
        }
    };

    public Comparator<Beer> ratingComparator = new Comparator<Beer>() {
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