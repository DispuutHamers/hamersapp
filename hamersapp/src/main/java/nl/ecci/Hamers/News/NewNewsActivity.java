package nl.ecci.Hamers.News;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

public class NewNewsActivity extends AppCompatActivity {

    private LinearLayout parentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_news_acitivity);

        parentLayout = (LinearLayout) findViewById(R.id.new_news_parent);

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    public void postNews(View view) {
        EditText news_title = (EditText) findViewById(R.id.news_title);
        EditText news_body = (EditText) findViewById(R.id.news_body);

        String title = news_title.getText().toString();
        String body = news_body.getText().toString();

        // Send request
        SendPostRequest req = new SendPostRequest(this, parentLayout, SendPostRequest.NEWSURL, PreferenceManager.getDefaultSharedPreferences(this), "news[title]=" + title + "&news[body]=" + body);
        req.execute();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
