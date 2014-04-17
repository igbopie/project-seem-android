package com.seem.android.mockup1.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seem.android.mockup1.R;
import com.seem.android.mockup1.customviews.SpinnerImageView;
import com.seem.android.mockup1.executor.MyAsyncTask;
import com.seem.android.mockup1.model.Feed;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.model.Media;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.service.MediaService;
import com.seem.android.mockup1.util.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 18/03/14.
 */
public class FeedAdapter extends ArrayAdapter<Feed> {

    private List<Feed> itemList;
    private Context context;
    private Map<View,List<AsyncTask>> processMap;

    private FetchLastItemListener fetchLastItemListener;

    public FeedAdapter(List<Feed> itemList, Context ctx) {
        super(ctx, R.layout.component_seem_list, itemList);
        this.itemList = itemList;
        this.context = ctx;
        processMap = new HashMap<View, List<AsyncTask>>();
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
        List<AsyncTask> toCancelTasks = processMap.get(convertView);
        if(toCancelTasks == null){
            toCancelTasks = new ArrayList<AsyncTask>();
            processMap.put(convertView,toCancelTasks);
        }

        for(AsyncTask task:toCancelTasks){
            task.cancel(true);
        }

        toCancelTasks.clear();

        //Common stuff
        TextView agentTextView = (TextView) convertView.findViewById(R.id.agentTextView);
        TextView dateTextView = (TextView)  convertView.findViewById(R.id.dateTextView);
        SpinnerImageView mainImageView = (SpinnerImageView) convertView.findViewById(R.id.mainImageView);

        agentTextView.setText("@"+feed.getUsername());
        long epoch = feed.getCreated().getTime();
        dateTextView.setText(DateUtils.getRelativeTimeSpanString(epoch,System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        Media media = new Media(feed.getItemMediaId());
        mainImageView.setText(feed.getItemCaption());
        mainImageView.setLoading(true);
        mainImageView.getImageView().setImageDrawable(null);
        FetchThumbs fetchThumbs1 = new FetchThumbs(mainImageView,media);
        toCancelTasks.add(fetchThumbs1);
        fetchThumbs1.execute();

        //TODO specific stuff
        if(fa == Feed.FeedAction.CREATE_SEEM) {
            TextView seemTitleTextView = (TextView) convertView.findViewById(R.id.seemTitleTextView);
            seemTitleTextView.setText(feed.getSeemTitle());

        }else if(fa == Feed.FeedAction.FAVOURITE) {

        }else if(fa == Feed.FeedAction.REPLY_TO) {
            TextView originalPostAuthor = (TextView) convertView.findViewById(R.id.originalPostAuthor);
            originalPostAuthor.setText("@"+feed.getReplyToUsername());
            SpinnerImageView originalPost = (SpinnerImageView) convertView.findViewById(R.id.originalPost);
            Media originalPostMedia =  new Media(feed.getReplyToMediaId());
            originalPost.setText(feed.getReplyToCaption());
            originalPost.setLoading(true);
            originalPost.getImageView().setImageDrawable(null);

            FetchThumbs fetchThumbs2 = new FetchThumbs(originalPost,originalPostMedia);
            toCancelTasks.add(fetchThumbs2);
            fetchThumbs2.execute();

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

    private class FetchThumbs extends AsyncTask<Void,Void,Void> {
        private SpinnerImageView imageView;
        private Media media;

        public FetchThumbs(SpinnerImageView imageView,Media media){
            this.imageView = imageView;
            this.media = media;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            MediaService.getInstance().getThumb(media);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(this.isCancelled()){
                //well... do not paint...
            }else if(media != null) {
                imageView.getImageView().setImageDrawable(media.getImageThumb());
                imageView.getImageView().setVisibility(View.VISIBLE);
                imageView.setLoading(false);
            }
            imageView = null;
            media = null;
        }
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