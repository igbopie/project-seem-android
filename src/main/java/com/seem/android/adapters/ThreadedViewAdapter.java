package com.seem.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.seem.android.R;
import com.seem.android.customviews.SquareImageView;
import com.seem.android.model.Item;
import com.seem.android.service.Api;
import com.seem.android.util.Utils;

import java.util.List;

/**
 * Created by igbopie on 18/03/14.
 */
public class ThreadedViewAdapter extends ArrayAdapter<Item> {

    private List<Item> itemList;
    private Context context;

    public ThreadedViewAdapter(List<Item> itemList, Context ctx) {
        super(ctx, R.layout.component_threaded_list, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public Item getItem(int position) {
        if (itemList != null)
            return itemList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (itemList != null)
            return itemList.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //v = inflater.inflate(R.layout.component_threaded_list, null);
            v = new ImageView(getContext());
            ((ImageView)v).setScaleType(ImageView.ScaleType.CENTER_CROP);

        }

        Item c = itemList.get(position);
        Utils.loadBitmap(c.getMediaId(), Api.ImageFormat.LARGE, (ImageView) v, getContext());
        /*ImageView imageView = (ImageView) v.findViewById(R.id.threadImageView);

        TextView depthTextView = (TextView) v.findViewById(R.id.depthTextView);
        depthTextView.setText(c.getDepth()+"");

        TextView captionTextView = (TextView) v.findViewById(R.id.captionTextView);
        captionTextView.setText(c.getCaption());

        Utils.loadBitmap(c.getMediaId(), Api.ImageFormat.THUMB,imageView,context);*/

        return v;

    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }




}