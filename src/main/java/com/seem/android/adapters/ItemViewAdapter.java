package com.seem.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.seem.android.customviews.ItemView;
import com.seem.android.model.Item;

import java.util.List;

/**
 * Created by igbopie on 28/05/14.
 */
public class ItemViewAdapter  extends BaseAdapter {

    private List<Item> itemList;
    private Context context;
    private ItemView.OnItemClickListener onItemClickListener;

    public ItemViewAdapter(List<Item> itemList, Context ctx,ItemView.OnItemClickListener onItemClickListener) {
        super();
        this.itemList = itemList;
        this.context = ctx;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return itemList.get(i).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null || convertView.getTag() == null || !convertView.getTag().equals(position)) {
            ItemView view;
            if (convertView == null) {
                view = new ItemView(context, null);
                view.setOnItemClickListener(onItemClickListener);
            } else {
                //Reusing views...
                view = (ItemView) convertView;
            }
            final Item item = (Item) getItem(position);
            ItemView.Theme theme= ItemView.Theme.REPLY;
            view.setItem(item,theme);


            return view;
        }

        return convertView;
    }

}
