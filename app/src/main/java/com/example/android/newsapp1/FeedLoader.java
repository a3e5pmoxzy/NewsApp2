package com.example.android.newsapp1;
import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class FeedLoader extends AsyncTaskLoader<List<com.example.android.newsapp1.Feed>> {

    private String mUrl;

    /**
     * Constructs a new {@link FeedLoader}.
     *
     * @param context of the activity.
     * @param url     to load feeds.
     */
    public FeedLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<com.example.android.newsapp1.Feed> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<com.example.android.newsapp1.Feed> feeds = com.example.android.newsapp1.QueryUtils.fetchFeedData(mUrl);
        return feeds;
    }
}
