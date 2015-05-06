package nl.ecci.Hamers.Helpers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.TypedValue;
import android.widget.Toast;
import nl.ecci.Hamers.Beers.BeerFragment;
import nl.ecci.Hamers.Events.EventFragment;
import nl.ecci.Hamers.MainActivity;
import nl.ecci.Hamers.News.NewsFragment;
import nl.ecci.Hamers.Quotes.QuoteListFragment;
import nl.ecci.Hamers.R;
import nl.ecci.Hamers.Users.UserFragment;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class GetJson extends AsyncTask<String, String, String> {
    public static final String QUOTEURL = "/quote.json";
    public static final String USERURL = "/user.json";
    public static final String EVENTURL = "/event.json";
    public static final String NEWSURL = "/news.json";
    public static final String BEERURL = "/beer.json";
    public static final String REVIEWURL = "/review.json";
    private static final boolean DEBUG = false;
    private Fragment f;
    private String typeURL;
    private SharedPreferences prefs;
    private Activity a;
    private boolean downloadBeerPicturesBool = false;
    private boolean downloadUserPicturesBool = false;
    private boolean firstload;

    public GetJson(Activity a, Fragment f, String typeURL, SharedPreferences s, Boolean firstload) {
        this.f = f;
        this.typeURL = typeURL;
        this.prefs = s;
        this.a = a;
        this.firstload = firstload;
    }

    public void setBeerPictureDownload() {
        downloadBeerPicturesBool = true;
    }

    public void setUserPictureDownload() {
        downloadUserPicturesBool = true;
    }

    protected String doInBackground(String... params) {
        BufferedReader reader;
        StringBuffer buffer = new StringBuffer();
        if (downloadBeerPicturesBool) {
            downloadBeerpictures(DataManager.getJsonArray(prefs, DataManager.BEERKEY));
        } else if (downloadUserPicturesBool) {
            downloadProfilepictures(DataManager.getJsonArray(prefs, DataManager.USERKEY));
        } else {
            try {
                URL url = new URL(MainActivity.baseURL + prefs.getString(DataManager.APIKEYKEY, "a") + typeURL);
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                int read;
                char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1) {
                    buffer.append(chars, 0, read);
                }
                reader.close();
            } catch (MalformedURLException e) {
                if (DEBUG) {
                    System.out.println("--------------------------Malformed URL!: ");
                    e.printStackTrace();
                }
                return null;
            } catch (IOException e) {
                if (DEBUG) {
                    System.out.println("--------------------------IOException!: ");
                    e.printStackTrace();
                }
                return null;
            }
            return buffer.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        if (result == null) {
            if (firstload) {
                ((MainActivity) a).loadData2(prefs, false);
            }
        } else {
            try {
                JSONArray arr = new JSONArray(result);
                if (arr.getJSONObject(0).has("error")) {
                    ((MainActivity) a).loadData2(prefs, false);
                }
                if (firstload && a instanceof MainActivity) {
                    ((MainActivity) a).loadData2(prefs, true);
                }
                if ((!downloadUserPicturesBool && !downloadBeerPicturesBool) && result.equals("{}")) {
                    Toast.makeText(a, a.getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
                } else {
                    if (f instanceof QuoteListFragment) {
                        prefs.edit().putString(DataManager.QUOTEKEY, result).apply();
                        MainActivity.quoteListFragment.populateList(prefs);
                    }
                    // User fragment
                    else if (f instanceof UserFragment) {
                        if (!downloadUserPicturesBool) {
                            prefs.edit().putString(DataManager.USERKEY, result).apply();
                            GetJson g = new GetJson(a, f, USERURL, prefs, false);
                            g.setUserPictureDownload();
                            g.execute();
                        }
                        MainActivity.userFragment.populateList(prefs);
                    }
                    // Event fragment
                    else if (f instanceof EventFragment) {
                        prefs.edit().putString(DataManager.EVENTKEY, result).apply();
                        MainActivity.eventFragment.populateList(prefs);
                    } else if (f instanceof NewsFragment) {
                        prefs.edit().putString(DataManager.NEWSKEY, result).apply();
                        MainActivity.newsFragment.populateList(prefs);
                    }
                    // Beer fragment
                    else if (f instanceof BeerFragment) {
                        if (!downloadBeerPicturesBool) {
                            prefs.edit().putString(DataManager.BEERKEY, result).apply();
                            GetJson g = new GetJson(a, f, BEERURL, prefs, false);
                            g.setBeerPictureDownload();
                            g.execute();
                            GetJson g2 = new GetJson(a, null, REVIEWURL, prefs, false);
                            g2.execute();
                        }
                        MainActivity.beerFragment.populateList(prefs);
                    }
                    // Quote
                    else if (typeURL.equals(QUOTEURL)) {
                        prefs.edit().putString(DataManager.QUOTEKEY, result).apply();
                        MainActivity.quoteListFragment.populateList(prefs);
                    }
                    // Beer
                    else if (typeURL.equals(BEERURL)) {
                        prefs.edit().putString(DataManager.BEERKEY, result).apply();
                        MainActivity.beerFragment.populateList(prefs);
                    }
                    // Review
                    else if (typeURL.equals(REVIEWURL)) {
                        prefs.edit().putString(DataManager.REVIEWKEY, result).apply();
                        GetJson g = new GetJson(a, f, BEERURL, prefs, false);
                        g.execute();
                    }
                }
            } catch (JSONException e) {
                if (firstload) {
                    ((MainActivity) a).loadData2(prefs, false);
                }
            }
        }
    }

    private void downloadProfilepictures(JSONArray users) {
        for (int i = 0; users != null && i < users.length(); i++) {
            try {
                if (DEBUG) {
                    System.out.println("downloading user picture " + i);
                }
                URL url = new URL("http://gravatar.com/avatar/" + MD5Util.md5Hex(users.getJSONObject(i).getString("email")));
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();
                prefs.edit().putString(DataManager.USERIMAGEKEY + users.getJSONObject(i).getString("id"), Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)).apply();
                //For restoring byte[] array = Base64.decode(stringFromSharedPrefs, Base64.DEFAULT);
            } catch (IOException e) {
                if (DEBUG) {
                    System.out.println("--------------------------IOException!: ");
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                if (DEBUG) {
                    System.out.println("--------------------------JSONException!: ");
                    e.printStackTrace();
                }
            }
        }
    }

    private void downloadBeerpictures(JSONArray beers) {
        for (int i = 0; beers != null && i < beers.length(); i++) {
            try {
                if (DEBUG) {
                    System.out.println("downloading beer picture " + i);
                }
                URL url = new URL(beers.getJSONObject(i).getString("picture"));
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();

                prefs.edit().putString(DataManager.BEERIMAGEKEY + beers.getJSONObject(i).getString("name"), Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)).apply();
                Bitmap bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length);

                Bitmap thumb = null;
                if (bitmap != null) {
                    thumb = scaleBitmap(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (beers.getJSONObject(i).getString("name").contains("jpg")) {
                        thumb.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    } else {
                        thumb.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    }
                    byte[] b = baos.toByteArray();
                    prefs.edit().putString(DataManager.BEERIMAGEKEY + beers.getJSONObject(i).getString("name") + "-thumb", Base64.encodeToString(b, Base64.DEFAULT)).apply();
                }
            } catch (MalformedURLException e) {
                if (DEBUG) {
                    System.out.println("--------------------------Malformed URL!: ");
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                if (DEBUG) {
                    System.out.println("--------------------------JSONException!: ");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                if (DEBUG) {
                    System.out.println("--------------------------IOException!: ");
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        // Converts 74 dip into its equivalent px
        Resources r = a.getResources();
        int maxWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74, r.getDisplayMetrics());
        int maxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74, r.getDisplayMetrics());

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) (height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) (width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        bm = Bitmap.createScaledBitmap(bm, width, height, false);
        return bm;
    }
}
