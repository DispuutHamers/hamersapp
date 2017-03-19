package nl.ecci.hamers.beers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.*
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_detail_item.*
import kotlinx.android.synthetic.main.row_review.view.*
import kotlinx.android.synthetic.main.stub_detail_beer.*
import nl.ecci.hamers.BuildConfig
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DataUtils
import nl.ecci.hamers.helpers.HamersActivity
import nl.ecci.hamers.helpers.SingleImageActivity
import nl.ecci.hamers.helpers.Utils
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.users.User
import java.util.*

class SingleBeerActivity : HamersActivity() {

    private var beer: Beer? = null
    private var user: User? = null
    private var ownReview: Review? = null

    // Activity for result
    internal var reviewRequestCode = 1
    internal var beerRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_item)

        initToolbar()

        // Intent by clicking an event in EventFragment or by clicking a link elsewhere
        val appLinkData = intent.data
        val beerID = intent.getIntExtra(Beer.BEER, Utils.notFound)
        if (appLinkData != null) {
            beer = DataUtils.getBeer(this, Utils.getIdFromUri(appLinkData))
        } else {
            beer = DataUtils.getBeer(this, beerID)
        }

        user = DataUtils.getOwnUser(this)

        initUI()

        getReviews()
    }

    private fun initUI() {
        stub_detail_item.layoutResource = R.layout.stub_detail_beer
        stub_detail_item.inflate()

        review_create_button.setOnClickListener { updateReview(ownReview) }

        var beerName = beer?.name
        if (BuildConfig.DEBUG) {
            beerName += " (" + beer?.id + ")"
        }
        beer_name.text = beerName

        fillDetailRow(row_kind, getString(R.string.beer_soort), beer?.kind)
        fillDetailRow(row_alc, getString(R.string.beer_alc), beer?.percentage)
        fillDetailRow(row_brewer, getString(R.string.beer_brewer), beer?.brewer)
        fillDetailRow(row_country, getString(R.string.beer_country), beer?.country)

        if (beer?.rating == null) {
            fillDetailRow(row_rating, getString(R.string.beer_rating), "Nog niet bekend")
        } else {
            fillDetailRow(row_rating, getString(R.string.beer_rating), beer?.rating)
        }

        ImageLoader.getInstance().displayImage(beer?.imageURL, beer_image)

        beer_image.setOnClickListener {
            val intent = Intent(this@SingleBeerActivity, SingleImageActivity::class.java)
            val transitionName = getString(R.string.transition_single_image)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SingleBeerActivity, beer_image, transitionName)
            intent.putExtra(Beer.BEER, gson.toJson(beer, Beer::class.java))
            ActivityCompat.startActivity(this@SingleBeerActivity, intent, options.toBundle())
        }

    }

    private fun getReviews() {
        val type = object : TypeToken<ArrayList<Review>>() {
        }.type

        val reviewList = ArrayList<Review>()
        val tempList = GsonBuilder().create().fromJson<ArrayList<Review>>(prefs?.getString(Loader.REVIEWURL, null), type)

        tempList.filterTo(reviewList) {
            it.beerID == beer?.id
        }

        val iterator = reviewList.listIterator()

        while (iterator.hasNext()) {
            val review = iterator.next()
            if (review.userID == user?.id) {
                review_create_button.setText(R.string.edit_review)
                ownReview = review
            }
            insertReview(review)
            if (iterator.hasNext()) {
                // Insert divider
                val divider = layoutInflater.inflate(R.layout.element_divider, review_insert_point, false)
                review_insert_point.addView(divider)
            }
        }

        if (reviewList.isEmpty()) {
            review_insert_point.removeAllViews()
        }
    }

    /**
     * Called when the user clicks the button to create a new beer review,
     * starts NewBeerActivity.
     */
    private fun updateReview(review: Review?) {
        val intent = Intent(this, NewReviewActivity::class.java)
        intent.putExtra(Beer.BEER, beer?.id)

        if (review != null) {
            intent.putExtra(Review.REVIEW, gson.toJson(review, Review::class.java))
        }

        startActivityForResult(intent, reviewRequestCode)
    }

    private fun insertReview(review: Review) {
        val view = layoutInflater.inflate(R.layout.row_review, review_insert_point, false)

        view.review_title.text = String.format("%s: ", DataUtils.getUser(this, review.userID).name)
        view.review_body.text = review.description
        view.review_date.text = MainActivity.appDTF.format(review.proefdatum)
        view.review_rating.text = String.format("Cijfer: %s", review.rating)

        // Insert into view
        review_insert_point.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        if (DataUtils.getOwnUser(this).id == review.userID) {
            registerForContextMenu(view)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
        // Edit review
            R.id.edit_item -> {
                updateReview(ownReview)
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Edit beer
            R.id.edit_item -> {
                val intent = Intent(this, NewBeerActivity::class.java)
                if (beer != null) {
                    intent.putExtra(Beer.BEER, beer?.id)
                }
                startActivityForResult(intent, beerRequestCode)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            getReviews()
            if (requestCode == reviewRequestCode) {
                val newBody = data?.getStringExtra(reviewBody)
                val newRating = data?.getIntExtra(reviewRating, -1)

                if (ownReview != null) {
                    for (i in 0..review_insert_point.childCount - 1) {
                        val view = review_insert_point.getChildAt(i)
                        if (view.id == R.id.review_row) { // Could be 'divider'
                            val bodyTextView = view.findViewById(R.id.review_body) as TextView
                            val ratingTextView = view.findViewById(R.id.review_rating) as TextView
                            if (bodyTextView.text === ownReview?.description) {
                                bodyTextView.text = newBody
                                ratingTextView.text = String.format("Cijfer: %s", newRating)
                            }
                        }
                    }
                }
            } else if (requestCode == beerRequestCode) {
                beer?.name = data?.getStringExtra(beerName).toString()
                beer?.kind = data?.getStringExtra(beerKind).toString()
                beer?.percentage = data?.getStringExtra(beerPercentage).toString()
                beer?.percentage = data?.getStringExtra(beerPercentage).toString()
                beer?.brewer = data?.getStringExtra(beerBrewer).toString()
                beer?.country = data?.getStringExtra(beerCountry).toString()
                beer?.rating = beer?.rating!! + " (Nog niet bijgewerkt)"

                initUI()
            }
        }
    }

    companion object {
        val reviewRating = "reviewRating"
        val reviewBody = "reviewBody"
        val beerName = "beerName"
        val beerKind = "beerKind"
        val beerPercentage = "beerPercentage"
        val beerBrewer = "beerBrewer"
        val beerCountry = "beerCountry"
    }
}
