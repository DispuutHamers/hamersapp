package nl.ecci.hamers.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail_user.*
import nl.ecci.hamers.R
import nl.ecci.hamers.models.User
import nl.ecci.hamers.utils.DataUtils.convertNicknames
import nl.ecci.hamers.utils.DataUtils.getGravatarURL
import nl.ecci.hamers.utils.DataUtils.getUser

class SingleUserActivity : HamersActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

        initToolbar()

        val user = getUser(this, intent.getIntExtra(User.USER, -1))

        collapsing_toolbar.title = user.name

        loadBackdrop(user)

        fillDetailRow(row_user_name, getString(R.string.user_name), user.name)
        fillDetailRow(row_user_quotecount, getString(R.string.user_quotecount), user.quoteCount.toString())
        fillDetailRow(row_user_reviewcount, getString(R.string.user_reviewcount), user.reviewCount.toString())
        fillDetailRow(row_user_batch, getString(R.string.user_batch), user.batch.toString())

        val nicknameRow = findViewById<View>(R.id.row_user_nickname)
        val nicknameDivider = findViewById<View>(R.id.user_nickname_divider)
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

        val emailRow = findViewById<LinearLayout>(R.id.row_user_email)
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
        Glide.with(this).load(getGravatarURL(user.email)).into(user_backdrop)
    }

}
