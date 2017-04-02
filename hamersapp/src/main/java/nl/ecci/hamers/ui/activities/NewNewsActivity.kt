package nl.ecci.hamers.ui.activities

import android.os.Bundle
import android.widget.DatePicker
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_general.*
import kotlinx.android.synthetic.main.stub_new_news.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.data.PostCallback
import org.json.JSONException
import org.json.JSONObject

class NewNewsActivity : NewItemActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general)

        initToolbar()

        stub.layoutResource = R.layout.stub_new_news
        stub.inflate()
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
