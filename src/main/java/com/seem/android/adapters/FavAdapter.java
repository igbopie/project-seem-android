package com.seem.android.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.customviews.SpinnerImageView;
import com.seem.android.model.Feed;
import com.seem.android.model.Item;
import com.seem.android.model.Media;
import com.seem.android.service.MediaService;
import com.seem.android.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 18/03/14.
 */
public class FavAdapter extends ArrayAdapter<Item> {

    private List<Item> itemList;
    private Context context;
    private Map<View,List<AsyncTask>> processMap;

    private FetchLastItemListener fetchLastItemListener;

    public FavAdapter(List<Item> itemList, Context ctx) {
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

    public Item getItem(int position) {
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

        Item item = itemList.get(position);

        if (convertView == null) {
            // You can move this line into your constructor, the inflater service won't change.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.component_feed_list_favourite, null);

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

        agentTextView.setText("@" + MyApplication.getUsername());
        dateTextView.setText(Utils.getRelativeTime(item.getFavouritedDate()));

        Media media = new Media(item.getMediaId());
        mainImageView.setText(item.getCaption());
        mainImageView.setLoading(true);
        mainImageView.getImageView().setImageDrawable(null);
        FetchThumbs fetchThumbs1 = new FetchThumbs(mainImageView,media);
        toCancelTasks.add(fetchThumbs1);
        fetchThumbs1.execute();




        return convertView;

    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
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