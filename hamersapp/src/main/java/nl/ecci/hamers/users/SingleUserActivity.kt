package nl.ecci.hamers.users

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import kotlinx.android.synthetic.main.detail_user.*
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DataUtils.convertNicknames
import nl.ecci.hamers.helpers.DataUtils.getGravatarURL
import nl.ecci.hamers.helpers.DataUtils.getUser
import nl.ecci.hamers.helpers.HamersActivity
import uk.co.senab.photoview.PhotoViewAttacher

class SingleUserActivity : HamersActivity() {
    private var mAttacher: PhotoViewAttacher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_user)

        initToolbar()

        val user = getUser(this, intent.getIntExtra(User.USER_ID, -1))

        collapsing_toolbar.title = user.name

        loadBackdrop(user)

        fillDetailRow(row_user_name, getString(R.string.user_name), user.name)
        fillDetailRow(row_user_quotecount, getString(R.string.user_quotecount), user.quoteCount.toString())
        fillDetailRow(row_user_reviewcount, getString(R.string.user_reviewcount), user.reviewCount.toString())
        fillDetailRow(row_user_batch, getString(R.string.user_batch), user.batch.toString())

        val nicknameRow = findViewById(R.id.row_user_nickname)
        val nicknameDivider = findViewById(R.id.user_nickname_divider)
        if (user.nicknames.size > 0) {
            fillDetailRow(nicknameRow, getString(R.string.user_nickname), convertNicknames(user.nicknames))
        } else if (nicknameRow != null && nicknameDivider != null) {
            nicknameRow.visibility = View.GONE
            nicknameDivider.visibility = View.GONE
        }

        if (user.member === User.Member.LID) {
            fillDetailRow(row_user_status, getString(R.string.user_status), getString(R.string.user_member))
        } else {
            fillDetailRow(row_user_status, getString(R.string.user_status), getString(R.string.user_member_ex))
        }

        val emailRow = findViewById(R.id.row_user_email)
        if (emailRow != null) {
            fillDetailRow(emailRow, getString(R.string.user_email), user.email)
            emailRow.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_SENDTO
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(user.email))
                startActivity(intent)
            }
        }
    }

    private fun loadBackdrop(user: User) {
        mAttacher = PhotoViewAttacher(user_backdrop)

        ImageLoader.getInstance().displayImage(getGravatarURL(user.email), user_backdrop, object : ImageLoadingListener {
            override fun onLoadingStarted(imageUri: String, view: View) {}

            override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {}

            override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
                mAttacher?.update()
            }

            override fun onLoadingCancelled(imageUri: String, view: View) {}
        })
    }

}
