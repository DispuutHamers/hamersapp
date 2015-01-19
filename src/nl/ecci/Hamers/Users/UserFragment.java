package nl.ecci.Hamers.Users;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.GetJson;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<User> listItems = new ArrayList<User>();
    ArrayAdapter<User> adapter;
    SwipeRefreshLayout swipeView;

    public UserFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);
        ListView user_list = (ListView) view.findViewById(R.id.users_listView);

        initSwiper(view, user_list);

        adapter = new UsersAdapter(this.getActivity(), listItems);
        user_list.setAdapter(adapter);

        return view;
    }

    public void initSwiper(View view, final ListView user_list) {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.users_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(this);

        user_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (user_list != null && user_list.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = user_list.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = user_list.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeView.setEnabled(enable);
            }
        });
    }

    @Override
    public void onRefresh() {
        GetJson g = new GetJson(this.getActivity(), this, GetJson.USERURL, PreferenceManager.getDefaultSharedPreferences(this.getActivity()), false);
        g.execute();
    }

    public void populateList(SharedPreferences prefs) {
        JSONArray json;
        try {
            if (prefs != null && (json = DataManager.getJsonArray(prefs, DataManager.USERKEY)) != null) {
                listItems.clear();
                for (int i = 0; i < json.length(); i++) {
                    JSONObject temp;
                    temp = json.getJSONObject(i);
                    User tempUser = new User(temp.getString("name"), temp.getInt("id"), temp.getInt("quotes"), temp.getInt("reviews"));
                    listItems.add(tempUser);
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
}
