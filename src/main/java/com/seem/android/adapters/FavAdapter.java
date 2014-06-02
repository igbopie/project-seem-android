package com.seem.android.adapters;

import android.content.Context;
import android.content.res.Resources;
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
import com.seem.android.service.Api;
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

    private FetchLastItemListener fetchLastItemListener;

    public FavAdapter(List<Item> itemList, Context ctx) {
        super(ctx, R.layout.component_seem_list, itemList);
        this.itemList = itemList;
        this.context = ctx;
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

        //Common stuff
        TextView agentTextView = (TextView) convertView.findViewById(R.id.agentTextView);
        TextView dateTextView = (TextView)  convertView.findViewById(R.id.dateTextView);
        SpinnerImageView mainImageView = (SpinnerImageView) convertView.findViewById(R.id.mainImageView);

        agentTextView.setText("@" + item.getUsername());
        dateTextView.setText(Utils.getRelativeTime(item.getFavouritedDate()));

        mainImageView.setText(item.getCaption());
        mainImageView.setLoading(false);

        Utils.loadBitmap(item.getMediaId(), Api.ImageFormat.THUMB,mainImageView.getImageView(),mainImageView.getHeight(),mainImageView.getHeight(),context);

        return convertView;

    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
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