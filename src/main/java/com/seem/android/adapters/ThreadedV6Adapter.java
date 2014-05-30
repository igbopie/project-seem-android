package com.seem.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.component_threaded_view_item_v6, null);
        }

        Item c = itemList.get(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.itemMainImage);
        ViewGroup.LayoutParams layout = imageView.getLayoutParams();
        if(layout == null){
            layout = new RelativeLayout.LayoutParams(parent.getLayoutParams().height,parent.getLayoutParams().height);
        }
        layout.width = parent.getLayoutParams().height;
        layout.height = parent.getLayoutParams().height;
        imageView.setLayoutParams(layout);

        Utils.loadBitmap(c.getMediaId(), Api.ImageFormat.THUMB, imageView, getContext());
        return convertView;

    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }




}