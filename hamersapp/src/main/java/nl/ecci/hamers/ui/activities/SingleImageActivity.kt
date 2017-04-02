package nl.ecci.hamers.ui.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_single_image.*
import nl.ecci.hamers.R
import nl.ecci.hamers.models.Beer
import uk.co.senab.photoview.PhotoViewAttacher

class SingleImageActivity : HamersActivity() {

    private var mAttacher: PhotoViewAttacher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_image)

        val beer: Beer? = GsonBuilder().create().fromJson(intent.getStringExtra(Beer.BEER), Beer::class.java)

        Glide.with(this).load(beer?.imageURL).fitCenter().into(image)
        mAttacher = PhotoViewAttacher(image)
    }
}
