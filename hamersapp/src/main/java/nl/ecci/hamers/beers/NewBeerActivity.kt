package nl.ecci.hamers.beers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText

import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.beer_new_activity.*

import org.json.JSONException
import org.json.JSONObject

import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.NewItemActivity
import nl.ecci.hamers.helpers.Utils
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.loader.PostCallback

class NewBeerActivity : NewItemActivity() {

    private var beerID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beer_new_activity)

        initToolbar()

        beerID = intent.getIntExtra(Beer.BEER, -1)
        if (beerID != -1) {
            val beer = Utils.getBeer(MainActivity.prefs, beerID)
            beer_name.setText(beer.name)
            beer_picture.setText(beer.imageURL)
            beer_soort.setText(beer.kind)
            beer_percentage.setText(beer.percentage)
            beer_brewer.setText(beer.brewer)
            beer_country.setText(beer.country)

            title = "Wijzig " + beer.name
        }
    }

    override fun postItem() {
        var percentage = beer_percentage.text.toString()

        if (!percentage.contains("%")) {
            percentage += "%"
        }

        val body = JSONObject()
        try {
            body.put("name", beer_name!!.text.toString())
            body.put("picture", beer_picture.text.toString())
            body.put("percentage", percentage)
            body.put("country", beer_country.text.toString())
            body.put("brewer", beer_brewer.text.toString())
            body.put("soort", beer_soort.text.toString())
        } catch (ignored: JSONException) {
        }

        Loader.postOrPatchData(this, Loader.BEERURL, body, beerID, object : PostCallback {
            override fun onSuccess(response: JSONObject) {
                val returnIntent = Intent()
                returnIntent.putExtra(SingleBeerActivity.beerName, beer_name.text.toString())
                returnIntent.putExtra(SingleBeerActivity.beerKind, beer_soort.text.toString())
                returnIntent.putExtra(SingleBeerActivity.beerPercentage, beer_percentage.text.toString())
                returnIntent.putExtra(SingleBeerActivity.beerBrewer, beer_brewer.text.toString())
                returnIntent.putExtra(SingleBeerActivity.beerCountry, beer_country.text.toString())
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }

            override fun onError(error: VolleyError) {
                disableLoadingAnimation()
            }
        })
    }
}