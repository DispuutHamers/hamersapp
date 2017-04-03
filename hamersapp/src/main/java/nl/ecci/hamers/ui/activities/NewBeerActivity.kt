package nl.ecci.hamers.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_general.*
import kotlinx.android.synthetic.main.stub_new_beer.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.data.PostCallback
import nl.ecci.hamers.models.Beer
import nl.ecci.hamers.utils.DataUtils
import org.json.JSONException
import org.json.JSONObject

class NewBeerActivity : HamersNewItemActivity() {

    private var beerID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()

        stub.layoutResource = R.layout.stub_new_beer
        stub.inflate()

        beerID = intent.getIntExtra(Beer.BEER, -1)
        if (beerID != -1) {
            val beer = DataUtils.getBeer(this, beerID)
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
                setResult(Activity.RESULT_OK, Intent())
                finish()
            }

            override fun onError(error: VolleyError) {
                disableLoadingAnimation()
            }
        })
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
    }
}