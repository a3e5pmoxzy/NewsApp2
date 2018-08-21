package com.example.android.newsapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<com.example.android.newsapp1.Feed> {

    /**
     * Our wn custom constructor.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param feeds   is the is the list of {@link com.example.android.newsapp1.Feed) to be displayd which is the data source for the adapter.
     */
    public NewsAdapter(Context context, List<com.example.android.newsapp1.Feed> feeds) {
        super(context, 0, feeds);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.cpromos_news_list_items, parent, false);
        }

        com.example.android.newsapp1.Feed currentFeed = getItem(position);
        TextView dateViewCustom = listItemView.findViewById(R.id.date);
        SimpleDateFormat formattedDateCustom = new SimpleDateFormat("dd.MM.yyyy");
        String formatDateCustom = formattedDateCustom.format(currentFeed.getDate());
        dateViewCustom.setText(formatDateCustom);
        TextView authorTextView = listItemView.findViewById(R.id.fullnameauthor);
        authorTextView.setText(currentFeed.getAuthorFullName());
        TextView sectionNameTextView = listItemView.findViewById(R.id.feedsection);
        sectionNameTextView.setText(currentFeed.getSectionName());
        TextView titleNameTextView = listItemView.findViewById(R.id.titlename);
        titleNameTextView.setText(currentFeed.getTitleName());
        return listItemView;
    }
}
