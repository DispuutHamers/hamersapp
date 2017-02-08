package nl.ecci.hamers.users

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.HamersActivity
import uk.co.senab.photoview.PhotoViewAttacher

import nl.ecci.hamers.helpers.Utils.convertNicknames
import nl.ecci.hamers.helpers.Utils.getGravatarURL
import nl.ecci.hamers.helpers.Utils.getUser

class SingleUserActivity : HamersActivity() {
    private var mAttacher: PhotoViewAttacher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_detail)

        initToolbar()

        val collapsingToolbar = findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout

        val user = getUser(MainActivity.prefs, intent.getIntExtra(User.USER_ID, -1))

        collapsingToolbar.title = user.name

        loadBackdrop(user)

        fillRow(findViewById(R.id.row_user_name), getString(R.string.user_name), user.name)
        fillRow(findViewById(R.id.row_user_quotecount), getString(R.string.user_quotecount), user.quoteCount.toString())
        fillRow(findViewById(R.id.row_user_reviewcount), getString(R.string.user_reviewcount), user.reviewCount.toString())
        fillRow(findViewById(R.id.row_user_batch), getString(R.string.user_batch), user.batch.toString())

        val nicknameRow = findViewById(R.id.row_user_nickname)
        val nicknameDivider = findViewById(R.id.user_nickname_divider)
        if (user.nicknames.size > 0) {
            fillRow(nicknameRow, getString(R.string.user_nickname), convertNicknames(user.nicknames))
        } else if (nicknameRow != null && nicknameDivider != null) {
            nicknameRow.visibility = View.GONE
            nicknameDivider.visibility = View.GONE
        }

        if (user.member === User.Member.LID) {
            fillRow(findViewById(R.id.row_user_status), getString(R.string.user_status), getString(R.string.user_member))
        } else {
            fillRow(findViewById(R.id.row_user_status), getString(R.string.user_status), getString(R.string.user_member_ex))
        }

        val emailRow = findViewById(R.id.row_user_email)
        if (emailRow != null) {
            fillRow(emailRow, getString(R.string.user_email), user.email)
            emailRow.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_SENDTO
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(user.email))
                startActivity(intent)
            }
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }
    }

    private fun loadBackdrop(user: User) {
        val imageView = findViewById(R.id.user_backdrop) as ImageView

        mAttacher = PhotoViewAttacher(imageView)

        ImageLoader.getInstance().displayImage(getGravatarURL(user.email), imageView, object : ImageLoadingListener {
            override fun onLoadingStarted(imageUri: String, view: View) {}

            override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {}

            override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
                if (mAttacher != null) {
                    mAttacher!!.update()
                }
            }

            override fun onLoadingCancelled(imageUri: String, view: View) {}
        })
    }

    private fun fillRow(view: View, title: String, description: String) {
        val titleView = view.findViewById(R.id.row_title) as TextView
        titleView.text = title

        val descriptionView = view.findViewById(R.id.row_description) as TextView
        descriptionView.text = description
    }
}
