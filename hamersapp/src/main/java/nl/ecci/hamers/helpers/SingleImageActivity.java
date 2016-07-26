package nl.ecci.hamers.helpers;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import nl.ecci.hamers.R;
import nl.ecci.hamers.beers.Beer;
import uk.co.senab.photoview.PhotoViewAttacher;

public class SingleImageActivity extends HamersActivity {

    private PhotoViewAttacher mAttacher;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        ImageView imageView = (ImageView) findViewById(R.id.beer_image);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Beer beer = gson.fromJson(getIntent().getStringExtra(Beer.BEER), Beer.class);

        // Universal Image Loader
        ImageLoader imageLoader = ImageLoader.getInstance();

        if (imageView != null) {
            imageLoader.displayImage(beer.getImageURL(), imageView, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (mAttacher != null) {
                        mAttacher.update();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            mAttacher = new PhotoViewAttacher(imageView);
        }

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(beer.getName());
        }
    }
}
