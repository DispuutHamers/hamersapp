package nl.ecci.hamers.news;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.EditText;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.NewItemActivity;
import nl.ecci.hamers.loader.Loader;
import nl.ecci.hamers.loader.PostCallback;

public class NewNewsActivity extends NewItemActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_new_acitivity);

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    public void postItem() {
        EditText news_title = (EditText) findViewById(R.id.news_title);
        EditText news_body = (EditText) findViewById(R.id.news_body);

        String title = news_title.getText().toString();
        String newsBody = news_body.getText().toString();

        JSONObject body = new JSONObject();
        try {
            body.put("name", title);
            body.put("body", newsBody);
            body.put("cat", "l");
        } catch (JSONException ignored) {
        }

        Loader.postOrPatchData(this, Loader.NEWSURL, body, -1, new PostCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                finish();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}
