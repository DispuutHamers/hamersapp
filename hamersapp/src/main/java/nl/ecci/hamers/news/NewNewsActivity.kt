package nl.ecci.hamers.news

import android.os.Bundle
import android.widget.DatePicker
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_new_item.*
import kotlinx.android.synthetic.main.stub_new_news.*
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.NewItemActivity
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.loader.PostCallback
import org.json.JSONException
import org.json.JSONObject

class NewNewsActivity : NewItemActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)

        initToolbar()

        stub_new_item.layoutResource = R.layout.stub_new_news
        stub_new_item.inflate()
    }

    override fun postItem() {
        val newsTitle = news_title.text.toString()
        val newsBody = news_body.text.toString()

        if (newsTitle.isNotBlank() && newsBody.isNotBlank())
        try {
            val body = JSONObject()
            body.put("title", newsTitle)
            body.put("body", newsBody)
            body.put("cat", "l")
            Loader.postOrPatchData(this, Loader.NEWSURL, body, -1, object : PostCallback {
                override fun onSuccess(response: JSONObject) {
                    finish()
                }

                override fun onError(error: VolleyError) {}
            })
        } catch (ignored: JSONException) {
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Nothing
    }
}
