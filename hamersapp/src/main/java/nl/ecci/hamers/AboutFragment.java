package nl.ecci.hamers;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import nl.ecci.hamers.helpers.Utils;
import us.feras.mdv.MarkdownView;

public class AboutFragment extends Fragment {
    private HashMap<String, String> libraries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        setHasOptionsMenu(true);

        MarkdownView markdownView = (MarkdownView) view.findViewById(R.id.mdv_changelog);
        markdownView.loadMarkdownFile("https://raw.githubusercontent.com/dexbleeker/hamersapp/master/CHANGELOG.md");
        markdownView.setBackgroundColor(0);

        String versionName = Utils.getAppVersion(getContext());

        TextView tv2 = (TextView) view.findViewById(R.id.about_txt2);
        tv2.setText(String.format("%s %s", getString(R.string.app_name), versionName));

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menu_libraries:
                showLibrariesDialog();
                return true;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.about_menu, menu);
    }

    private void buildLibrariesList() {
        if (this.libraries != null) {
            return;
        }

        this.libraries = new HashMap<>();
        this.libraries.put("Universal Image Loader", "https://github.com/nostra13/Android-Universal-Image-Loader");
        this.libraries.put("PhotoView", "https://github.com/chrisbanes/PhotoView");
        this.libraries.put("CircleImageView", "https://github.com/hdodenhof/CircleImageView");
        this.libraries.put("Volley", "https://android.googlesource.com/platform/frameworks/volley/");
        this.libraries.put("MarkdownView", "https://github.com/falnatsheh/MarkdownView");
    }

    private void showLibrariesDialog() {
        buildLibrariesList();

        CharSequence[] libs = Utils.stringArrayToCharSequenceArray(libraries.keySet().toArray());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.about_libraries)
                .setNeutralButton(R.string.close, null)
                .setItems(libs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String libraryName = (String) libraries.keySet().toArray()[i];
                        String target = libraries.get(libraryName);

                        if (target == null)
                            return;

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(target));
                        startActivity(browserIntent);
                    }
                })
                .show();
    }
}