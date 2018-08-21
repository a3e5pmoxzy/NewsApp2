package com.example.android.newsapp1;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the feed JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    public static List<com.example.android.newsapp1.Feed> fetchFeedData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<com.example.android.newsapp1.Feed> feeds = extractFeatureFromJson(jsonResponse);
        return feeds;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<com.example.android.newsapp1.Feed> extractFeatureFromJson(String feedJSON) {
        if (TextUtils.isEmpty(feedJSON)) {
            return null;
        }

        List<com.example.android.newsapp1.Feed> feeds = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(feedJSON);
            JSONObject currentBaseJsonResponse = baseJsonResponse.getJSONObject("response");
            JSONArray feedArray = currentBaseJsonResponse.getJSONArray("results");
            String mDateISO8601UtcObject;
            Date mDate;
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject currentFeed = feedArray.getJSONObject(i);
                JSONArray tagsArray = currentFeed.getJSONArray("tags");
                String url = currentFeed.optString("webUrl");
                mDateISO8601UtcObject = currentFeed.optString("webPublicationDate").substring(0, 10);
                mDate = ChUtils.chgFromISO8601UTC(mDateISO8601UtcObject, "yyyy-MM-dd");
                String titleName = currentFeed.optString("webTitle");
                String sectionName = currentFeed.optString("sectionName");
                String authorFullName = null;
                if (tagsArray != null && tagsArray.length() > 0) {

                    JSONObject itemTagContributor = tagsArray.getJSONObject(0);
                    authorFullName = itemTagContributor.getString("webTitle");
                }

                com.example.android.newsapp1.Feed feed = new com.example.android.newsapp1.Feed(mDate, authorFullName, sectionName, titleName, url);
                feeds.add(feed);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the feed JSON results", e);
        }
        return feeds;
    }
}
