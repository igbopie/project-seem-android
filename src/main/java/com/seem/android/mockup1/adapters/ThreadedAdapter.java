package com.seem.android.mockup1.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seem.android.mockup1.R;
import com.seem.android.mockup1.model.Seem;

import com.seem.android.mockup1.model.Item;

import java.util.List;

/**
 * Created by igbopie on 18/03/14.
 */
public class ThreadedAdapter extends ArrayAdapter<Item> {

    private List<Item> itemList;
    private Context context;

    public ThreadedAdapter(List<Item> itemList, Context ctx) {
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.component_threaded_list, null);
        }

        Item c = itemList.get(position);
        ImageView imageView = (ImageView) v.findViewById(R.id.threadImageView);
        imageView.setImageDrawable(c.getMedia().getImageThumb());

        TextView depthTextView = (TextView) v.findViewById(R.id.depthTextView);
        depthTextView.setText(c.getDepth()+"");

        TextView captionTextView = (TextView) v.findViewById(R.id.captionTextView);
        captionTextView.setText(c.getCaption());

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