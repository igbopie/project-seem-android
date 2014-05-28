package com.seem.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.seem.android.GlobalVars;
import com.seem.android.R;
import com.seem.android.customviews.ItemView;
import com.seem.android.customviews.SpinnerImageViewV5;
import com.seem.android.model.Item;
import com.seem.android.service.Api;
import com.seem.android.util.Utils;

import java.util.List;

/**
 * Created by igbopie on 28/05/14.
 */
public class ItemViewAdapter  extends BaseAdapter {

    private List<Item> itemList;
    private Context context;

    public ItemViewAdapter(List<Item> itemList, Context ctx) {
        super();
        this.itemList = itemList;
        this.context = ctx;
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
            } else {
                //Reusing views...
                view = (ItemView) convertView;
            }
            final Item item = (Item) getItem(position);
            ItemView.Theme theme;
            if(position == 0){
                theme = ItemView.Theme.MAIN;
            }else{
                theme = ItemView.Theme.REPLY;
            }
            view.setItem(item,theme);


            return view;
        }

        return convertView;
    }
}
