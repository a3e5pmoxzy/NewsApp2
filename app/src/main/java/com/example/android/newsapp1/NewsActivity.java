package com.example.android.newsapp1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Displays a {@link ViewPager} where each page shows a different section of the news
 */
public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<com.example.android.newsapp1.Feed>> {

    private TextView mEmptyStateTextView;
    private NewsAdapter mAdapter;
    private boolean isIntentSafe;
    private Date minDateObj;
    private Date maxDateObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        ListView feedListView = findViewById(R.id.sec_cpromos_news_list);
        mAdapter = new com.example.android.newsapp1.NewsAdapter(this, new ArrayList<com.example.android.newsapp1.Feed>());
        feedListView.setAdapter(mAdapter);

        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                com.example.android.newsapp1.Feed currentFeed = mAdapter.getItem(position);
                Uri feedUri = Uri.parse(currentFeed.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, feedUri);
                PackageManager packageMgr = getBaseContext().getPackageManager();
                List<ResolveInfo> activities = packageMgr.queryIntentActivities(
                        websiteIntent, 0);
                isIntentSafe = (activities.size() > 0);

                if (isIntentSafe) {
                    getBaseContext().startActivity(websiteIntent);
                }
            }
        });
        mEmptyStateTextView = findViewById(R.id.empty_view);
        feedListView.setEmptyView(mEmptyStateTextView);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(VarUtils.FEED_LOADER_ID, null, this);
        } else {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<com.example.android.newsapp1.Feed>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String maxFeed = sharedPrefs.getString(
                getString(R.string.settings_max_feed_key),
                getString(R.string.settings_max_feed_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        String minDateFeed = sharedPrefs.getString(
                getString(R.string.settings_min_date_feed_key),
                getString(R.string.settings_min_date_feed_default));

        String maxDateFeed = sharedPrefs.getString(
                getString(R.string.settings_max_date_feed_key),
                getString(R.string.settings_max_date_feed_default));

        String sectionName = sharedPrefs.getString(
                getString(R.string.settings_section_feed_key),
                getString(R.string.settings_section_feed_default)
        );

        try {
            if (!minDateFeed.equals(""))
                minDateObj = ChUtils.chgFromISO8601UTC(minDateFeed, "dd/MM/yyyy");
            SimpleDateFormat formatDateObjectISO8601 = new SimpleDateFormat("yyyy-MM-dd");
            minDateFeed = formatDateObjectISO8601.format(minDateObj);
        } catch (Exception e) {
            minDateFeed = "";
            Log.e("LOG_TAG", "Problem with the minimum date query parameter.", e);
        }

        try {
            if (!maxDateFeed.equals(""))
                maxDateObj = ChUtils.chgFromISO8601UTC(maxDateFeed, "dd/MM/yyyy");
            SimpleDateFormat formatDateObjectISO8601 = new SimpleDateFormat("yyyy-MM-dd");
            maxDateFeed = formatDateObjectISO8601.format(maxDateObj);
        } catch (Exception e) {
            maxDateFeed = "";
            Log.e("LOG_TAG", "Problem with the maximum date query parameter.", e);
        }

        Uri baseUri = Uri.parse(VarUtils.JSON_REQUEST_URL1);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("q", VarUtils.JSON_REQUEST_QUERY1);
        uriBuilder.appendQueryParameter("show-tags", VarUtils.JSON_REQUEST_CONTRIBUTOR1);
        uriBuilder.appendQueryParameter("tag", VarUtils.JSON_REQUEST_TAG1);

        if (!maxFeed.equals("")) {
            uriBuilder.appendQueryParameter("page-size", maxFeed);
        }
        if (!minDateFeed.equals("")) {
            uriBuilder.appendQueryParameter("from-date", minDateFeed);
        }
        if (!maxDateFeed.equals("")) {
            uriBuilder.appendQueryParameter("to-date", maxDateFeed);
        }
        uriBuilder.appendQueryParameter("order-by", orderBy);
        if (!sectionName.equals("all")) {
            uriBuilder.appendQueryParameter("section", sectionName);
        }
        uriBuilder.appendQueryParameter("api-key", VarUtils.JSON_REQUEST_API1);
        return new FeedLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<com.example.android.newsapp1.Feed>> loader, List<com.example.android.newsapp1.Feed> feeds) {
        mEmptyStateTextView.setText(R.string.no_feeds);
        if (mAdapter != null) {
            mAdapter.clear();
        }

        if (feeds != null && !feeds.isEmpty()) {
            mAdapter.addAll(feeds);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<com.example.android.newsapp1.Feed>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
