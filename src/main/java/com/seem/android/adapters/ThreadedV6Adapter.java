package com.seem.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.seem.android.R;
import com.seem.android.customviews.SquareImageView;
import com.seem.android.model.Item;
import com.seem.android.service.Api;
import com.seem.android.util.Utils;

import java.util.List;

import it.sephiroth.android.library.widget.AbsHListView;

/**
 * Created by igbopie on 18/03/14.
 */
public class ThreadedV6Adapter extends ArrayAdapter<Item> {

    private List<Item> itemList;
    private Context context;

    public ThreadedV6Adapter(List<Item> itemList, Context ctx) {
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
            v = new SquareImageView(getContext());
            ((SquareImageView)v).setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewGroup.LayoutParams layout = v.getLayoutParams();
            if(layout == null){
                layout = new AbsHListView.LayoutParams(parent.getLayoutParams().height,parent.getLayoutParams().height);
            }
            layout.width = parent.getLayoutParams().height;
            layout.height = parent.getLayoutParams().height;
            v.setLayoutParams(layout);
        }

        Item c = itemList.get(position);



        Utils.loadBitmap(c.getMediaId(), Api.ImageFormat.THUMB, (ImageView) v, getContext());
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