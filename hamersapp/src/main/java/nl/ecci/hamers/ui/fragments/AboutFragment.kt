package nl.ecci.hamers.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.TextView
import nl.ecci.hamers.R
import nl.ecci.hamers.utils.Utils
import org.jetbrains.anko.support.v4.act
import us.feras.mdv.MarkdownView
import java.util.*

class AboutFragment : HamersFragment() {
    private var libraries: HashMap<String, String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        setHasOptionsMenu(true)

        val markdownView = view.findViewById<MarkdownView>(R.id.mdv_changelog) as MarkdownView
        markdownView.loadMarkdownFile("https://raw.githubusercontent.com/dexbleeker/hamersapp/master/CHANGELOG.md")
        markdownView.setBackgroundColor(0)

        val versionName = Utils.getAppVersion(context)

        val tv2 = view.findViewById<TextView>(R.id.about_txt2) as TextView
        tv2.text = String.format("%s %s", getString(R.string.app_name), versionName)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.menu_libraries -> {
                showLibrariesDialog()
                return true
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.about_menu, menu)
    }

    private fun buildLibrariesList() {
        if (this.libraries != null) {
            return
        }

        this.libraries = HashMap()
        this.libraries?.put("Universal Image Loader", "https://github.com/nostra13/Android-Universal-Image-Loader")
        this.libraries?.put("PhotoView", "https://github.com/chrisbanes/PhotoView")
        this.libraries?.put("CircleImageView", "https://github.com/hdodenhof/CircleImageView")
        this.libraries?.put("Volley", "https://android.googlesource.com/platform/frameworks/volley/")
        this.libraries?.put("MarkdownView", "https://github.com/falnatsheh/MarkdownView")
    }

    private fun showLibrariesDialog() {
        buildLibrariesList()

        val libs = Utils.stringArrayToCharSequenceArray(libraries!!.keys.toTypedArray())

        val builder = AlertDialog.Builder(act)
        builder.setTitle(R.string.about_libraries)
                .setNeutralButton(R.string.close, null)
                .setItems(libs, DialogInterface.OnClickListener { _, i ->
                    val target = libraries!![libraries!!.keys.toTypedArray()[i]] ?: return@OnClickListener

                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(target))
                    startActivity(browserIntent)
                })
                .show()
    }
}