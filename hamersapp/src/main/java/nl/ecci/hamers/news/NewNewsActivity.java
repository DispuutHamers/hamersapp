package nl.ecci.hamers.news;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;

public class NewNewsActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_news_acitivity);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    public void postNews(View view) {
        try {
            EditText news_title = (EditText) findViewById(R.id.news_title);
            EditText news_body = (EditText) findViewById(R.id.news_body);

            String title = URLEncoder.encode(news_title.getText().toString(), "UTF-8");
            String body = URLEncoder.encode(news_body.getText().toString(), "UTF-8");

            Map<String, String> params = new HashMap<>();
            params.put("news[title]", title);
            params.put("news[body]", body);

            DataManager.postData(this, prefs, DataManager.NEWSURL, DataManager.NEWSKEY, params);
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
