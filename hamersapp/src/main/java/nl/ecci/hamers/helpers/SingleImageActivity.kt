package nl.ecci.hamers.helpers

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.google.gson.GsonBuilder
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import kotlinx.android.synthetic.main.activity_single_image.*
import nl.ecci.hamers.R
import nl.ecci.hamers.beers.Beer
import uk.co.senab.photoview.PhotoViewAttacher

class SingleImageActivity : HamersActivity() {

    private var mAttacher: PhotoViewAttacher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_image)

        setSupportActionBar(toolbar as Toolbar?)

        val beer = GsonBuilder().create().fromJson(intent.getStringExtra(Beer.BEER), Beer::class.java)

        ImageLoader.getInstance().displayImage(beer.imageURL, beer_image, object : ImageLoadingListener {

            override fun onLoadingStarted(imageUri: String, view: View) {

            }

            override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {

            }

            override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
                mAttacher?.update()
            }

            override fun onLoadingCancelled(imageUri: String, view: View) {

            }
        })
        mAttacher = PhotoViewAttacher(beer_image)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = beer.name
    }
}
