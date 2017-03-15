package nl.ecci.hamers.beers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_new_item.*
import kotlinx.android.synthetic.main.stub_new_review.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DataUtils
import nl.ecci.hamers.helpers.DatePickerFragment
import nl.ecci.hamers.helpers.NewItemActivity
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.loader.PostCallback
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class NewReviewActivity : NewItemActivity() {

    private var beer: Beer? = null
    private var review: Review? = null
    private var rating = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)

        initToolbar()

        stub.layoutResource = R.layout.stub_new_review
        stub.inflate()

        val date_button = findViewById(R.id.pick_date_button) as Button
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", MainActivity.locale)

        val gson = GsonBuilder().create()
        beer = DataUtils.getBeer(this, intent.getIntExtra(Beer.BEER, 1))
        review = gson.fromJson<Review>(intent.getStringExtra(Review.REVIEW), Review::class.java)

        review_title.text = beer!!.name

        ratingseekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, rating: Int, fromUser: Boolean) {
                this@NewReviewActivity.rating = rating + 1
                review_rating.text = String.format("Cijfer: %s", this@NewReviewActivity.rating)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        if (review != null) {
            setTitle(R.string.review_update)
            review_body.setText(review!!.description)
            ratingseekbar.progress = review!!.rating - 1
            date_button.text = dateFormat.format(review!!.proefdatum)
        } else {
            date_button.text = dateFormat.format(calendar.time)
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle(R.string.new_review)
    }


    fun showDatePickerDialog(v: View) {
        val datePicker = DatePickerFragment()
        datePicker.show(supportFragmentManager, "proefdatum")
    }

    override fun postItem() {
        val review_body = this.review_body.text.toString()
        val date = pick_date_button.text.toString()

        if (review_body.length > 2) {
            val body = JSONObject()
            try {
                body.put("beer_id", beer!!.id)
                body.put("description", review_body)
                body.put("rating", rating)
                body.put("proefdatum", MainActivity.parseDate(date))

                var reviewID = -1
                if (review != null) {
                    reviewID = review!!.id
                }
                Loader.postOrPatchData(this, Loader.REVIEWURL, body, reviewID, object : PostCallback {
                    override fun onSuccess(response: JSONObject) {
                        val returnIntent = Intent()
                        returnIntent.putExtra(SingleBeerActivity.Companion.reviewBody, review_body)
                        returnIntent.putExtra(SingleBeerActivity.Companion.reviewRating, rating)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    override fun onError(error: VolleyError) {
                        disableLoadingAnimation()
                    }
                })
            } catch (ignored: JSONException) {
            }

        } else {
            disableLoadingAnimation()
            Toast.makeText(this, getString(R.string.missing_fields), Snackbar.LENGTH_LONG).show()
        }
    }
}
