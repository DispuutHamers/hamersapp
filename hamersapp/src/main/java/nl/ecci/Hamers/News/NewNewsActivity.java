package nl.ecci.Hamers.News;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

public class NewNewsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_news_acitivity);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void postNews(View view) {
        EditText news_title = (EditText) findViewById(R.id.news_title);
        EditText news_body = (EditText) findViewById(R.id.news_body);

        String title = news_title.getText().toString();
        String body = news_body.getText().toString();

        // Send request
        SendPostRequest req = new SendPostRequest(this, SendPostRequest.NEWSURL, PreferenceManager.getDefaultSharedPreferences(this), "news[title]=" + title + "&news[body]=" + body);
        req.execute();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
