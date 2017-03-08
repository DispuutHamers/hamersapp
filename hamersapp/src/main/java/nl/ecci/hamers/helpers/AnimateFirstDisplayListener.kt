package nl.ecci.hamers.helpers

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener

import java.util.Collections
import java.util.LinkedList

class AnimateFirstDisplayListener : SimpleImageLoadingListener() {

    override fun onLoadingComplete(imageUri: String, view: View?, loadedImage: Bitmap?) {
        if (loadedImage != null) {
            val imageView = view as ImageView?
            val firstDisplay = !displayedImages.contains(imageUri)
            if (firstDisplay) {
                FadeInBitmapDisplayer.animate(imageView, 500)
                displayedImages.add(imageUri)
            }
        }
    }

    companion object {

        val displayedImages: MutableList<String> = Collections.synchronizedList(LinkedList<String>())
    }
}
