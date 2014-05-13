package com.seem.android.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seem.android.R;
import com.seem.android.customviews.SpinnerImageView;
import com.seem.android.model.Feed;
import com.seem.android.service.Api;
import com.seem.android.util.Utils;
import java.util.List;

/**
 * Created by igbopie on 18/03/14.
 */
public class FeedAdapter extends ArrayAdapter<Feed> {

    private List<Feed> itemList;
    private Context context;

    private FetchLastItemListener fetchLastItemListener;

    public FeedAdapter(List<Feed> itemList, Context ctx) {
        super(ctx, R.layout.component_seem_list, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public Feed getItem(int position) {
        if(itemList != null){
            if(position == itemList.size()-1 && this.fetchLastItemListener != null){
                fetchLastItemListener.lastItemFetched();
            }

            return itemList.get(position);
        }

        return null;
    }

    public long getItemId(int position) {
        if (itemList != null)
            return itemList.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Feed feed = itemList.get(position);
        Feed.FeedAction fa = Feed.FeedAction.values()[getItemViewType(position)];

        if (convertView == null) {
            // You can move this line into your constructor, the inflater service won't change.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(fa == Feed.FeedAction.CREATE_SEEM) {
                convertView = inflater.inflate(R.layout.component_feed_list_create_seem, null);
            }else if(fa == Feed.FeedAction.FAVOURITE) {
                convertView = inflater.inflate(R.layout.component_feed_list_favourite, null);
            }else if(fa == Feed.FeedAction.REPLY_TO) {
                convertView = inflater.inflate(R.layout.component_feed_list_reply, null);
            }
        }

        //Common stuff
        TextView agentTextView = (TextView) convertView.findViewById(R.id.agentTextView);
        TextView dateTextView = (TextView)  convertView.findViewById(R.id.dateTextView);
        SpinnerImageView mainImageView = (SpinnerImageView) convertView.findViewById(R.id.mainImageView);

        agentTextView.setText("@"+feed.getUsername());
        dateTextView.setText(Utils.getRelativeTime(feed.getCreated()));

        mainImageView.setText(feed.getItemCaption());
        mainImageView.setLoading(false);

        Utils.loadBitmap(feed.getItemMediaId(), Api.ImageFormat.THUMB,mainImageView.getImageView(),context);

        //TODO specific stuff
        if(fa == Feed.FeedAction.CREATE_SEEM) {
            TextView seemTitleTextView = (TextView) convertView.findViewById(R.id.seemTitleTextView);
            seemTitleTextView.setText(feed.getSeemTitle());

        }else if(fa == Feed.FeedAction.FAVOURITE) {

        }else if(fa == Feed.FeedAction.REPLY_TO) {
            TextView originalPostAuthor = (TextView) convertView.findViewById(R.id.originalPostAuthor);
            originalPostAuthor.setText("@"+feed.getReplyToUsername());
            SpinnerImageView originalPost = (SpinnerImageView) convertView.findViewById(R.id.originalPost);
            originalPost.setText(feed.getReplyToCaption());
            originalPost.setLoading(false);

            Utils.loadBitmap(feed.getReplyToMediaId(), Api.ImageFormat.THUMB,originalPost.getImageView(),context);

        }


        return convertView;

    }

    public List<Feed> getItemList() {
        return itemList;
    }

    public void setItemList(List<Feed> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        // Define a way to determine which layout to use, here it's just evens and odds.
        return this.getItem(position).getAction().ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return Feed.FeedAction.values().length; // Count of different layouts
    }


    public FetchLastItemListener getFetchLastItemListener() {
        return fetchLastItemListener;
    }

    public void setFetchLastItemListener(FetchLastItemListener fetchLastItemListener) {
        this.fetchLastItemListener = fetchLastItemListener;
    }

    public interface FetchLastItemListener {
        public void lastItemFetched();
    }

}