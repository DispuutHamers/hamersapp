package com.ecci.Hamers;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class MainActivity extends Activity {
    // Drawer list
    private String[] mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Test Button
        Button button = (Button) findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            }
        });

        // Drawer list
        mDrawerItems = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }
}