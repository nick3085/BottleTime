package com.nickderonde.bottletime.utilities;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;


public class NetworkUtils {

    public interface NetworkDelegate {
        void randomProduct(Map<String, Object> product);
    }

    private final static String LOG_TAG = "Bottle Time Utils";

    private final static String LCBO_BASE_URL = "https://lcboapi.com";

    private final static String PARAM_PRODUCTS = "products";

    private final static String  PARAM_PAGE = "page";

    public static NetworkDelegate delegate = null;

    private final static String PARAM_ACCES =  "access_key";

    private final static String ACCES_KEY = "MDo4MjliYTNhZS01Y2MyLTExZTctYWM0OS0wM2MxYzIxZmMyZTM6SVJsY2Y3Qno4cU5XU0R4YkZCYWVQVWczSmFSSTBhYXU1UWhB";

    private static int lastPage = 0;
    private static int lastRecordNumber = 0;



    private static Map<String, Object> getRandomProductRecordNumber() {
        Random r = new Random();
        int min = 0;
        int max = 11486-1;
        int itemsPerPage = 20;
        int currentRecord = r.nextInt(max-min) + min;

        Log.e(NetworkUtils.LOG_TAG, String.valueOf(currentRecord));

        int page = 0;
        int recordNumber = 0;

        while (currentRecord > 0) {

            if (currentRecord - itemsPerPage >= 0) {
                currentRecord -= itemsPerPage;
                page++;
            } else  {
                recordNumber = currentRecord;
                currentRecord = 0;
            }
        }

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("page", page);
        results.put("recordNumber", recordNumber);

        return results;
    }

    private static URL buildUrl() {

        Map<String, Object> randomPageAndRecordNumber = getRandomProductRecordNumber();
        lastPage = (int)randomPageAndRecordNumber.get("page");
        lastRecordNumber = (int)randomPageAndRecordNumber.get("recordNumber");

        Uri builtUri = Uri.parse(LCBO_BASE_URL).buildUpon()
                .appendPath(PARAM_PRODUCTS)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(lastPage))
                .appendQueryParameter(PARAM_ACCES, ACCES_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
            Log.e(NetworkUtils.LOG_TAG, "buildUrl: Try Exception");

        } catch (MalformedURLException e) {
            Log.e(NetworkUtils.LOG_TAG, "buildUrl: Catch Exception");
            e.printStackTrace();
        }

        Log.e(NetworkUtils.LOG_TAG, String.valueOf(lastPage));
        Log.e(NetworkUtils.LOG_TAG, String.valueOf(lastRecordNumber));
        return url;
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static void makeLcboQuery() {

        URL lcboUrl = NetworkUtils.buildUrl();
        new BottleDownloaderTask().execute(lcboUrl);
    }

    /**
     * AsyncTask BottleDownloaderTask
     *
     */
    private static class BottleDownloaderTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String lcboResults = null;
            try {
                lcboResults = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lcboResults;
        }

        @Override
        protected void onPostExecute(String lcboResults) {

            try {
                JSONObject lcboResultsJSONObject =  new JSONObject(lcboResults);
                Map<String, Object> data = CollectionUtils.toMap(lcboResultsJSONObject);
                List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) data.get("result");
                Map<String, Object> product = result.get(lastRecordNumber);
                if (delegate != null) {
                    delegate.randomProduct(product);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
