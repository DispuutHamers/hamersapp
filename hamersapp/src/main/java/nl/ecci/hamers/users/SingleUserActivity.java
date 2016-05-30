package nl.ecci.hamers.users;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import nl.ecci.hamers.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class SingleUserActivity extends AppCompatActivity {

    private String username;
    private int userID;
    private String userEmail;
    private int userQuoteCount;
    private int userReviewCount;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        username = getIntent().getStringExtra(User.USER_NAME);
        userID = getIntent().getIntExtra(User.USER_ID, 1);
        userEmail = getIntent().getStringExtra(User.USER_EMAIL);
        userQuoteCount = getIntent().getIntExtra(User.USER_QUOTECOUNT, 0);
        userReviewCount = getIntent().getIntExtra(User.USER_REVIEWCOUNT, 0);

        collapsingToolbar.setTitle(username);

        loadBackdrop();

        fillRow(findViewById(R.id.row_user_name), getString(R.string.user_name), username);
        fillRow(findViewById(R.id.row_user_email), getString(R.string.user_email), userEmail);
        fillRow(findViewById(R.id.row_user_quotecount), getString(R.string.user_quotecount), String.valueOf(userQuoteCount));
        fillRow(findViewById(R.id.row_user_reviewcount), getString(R.string.user_reviewcount), String.valueOf(userReviewCount));
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);

        mAttacher = new PhotoViewAttacher(imageView);

        ImageLoader.getInstance().displayImage(getIntent().getStringExtra(User.USER_IMAGE_URL), imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {}

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (mAttacher != null) {
                    mAttacher.update();
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {}
        });
    }

    private void fillRow(View view, final String title, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.row_title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.row_description);
        descriptionView.setText(description);
    }
}
