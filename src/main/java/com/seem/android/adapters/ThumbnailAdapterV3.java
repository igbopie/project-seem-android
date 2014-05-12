package com.seem.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.seem.android.GlobalVars;
import com.seem.android.customviews.SpinnerImageView;
import com.seem.android.model.Item;
import com.seem.android.util.ItemSelectedListener;
import com.seem.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 24/03/14.
 */
public class ThumbnailAdapterV3 extends BaseAdapter {

    Context context;
    List<Item> items = new ArrayList<Item>();
    ItemSelectedListener repliesListener;
    ItemSelectedListener threadViewListener;
    public ThumbnailAdapterV3(Context context, ItemSelectedListener repliesListener, ItemSelectedListener threadViewListener) {
        super();
        this.context = context;
        this.repliesListener = repliesListener;
        this.threadViewListener = threadViewListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null || convertView.getTag() == null || !convertView.getTag().equals(position)) {
            SpinnerImageView view;
            if (convertView == null) {
                view = new SpinnerImageView(context, null);
                view.setLayoutParams(new GridView.LayoutParams(GlobalVars.GRID_SIZE_V2, GlobalVars.GRID_SIZE_V2));

            } else {
                //Reusing views...
                view = (SpinnerImageView) convertView;
                view.setLoading(true);
                view.getImageView().setVisibility(View.INVISIBLE);
                view.setRepliesNumber(0);
            }
            final Item item = (Item) getItem(position);

            view.setText(item.getCaption());
            view.setRepliesNumber(item.getReplyCount());
            if(item.getReplyCount() > 0) {
                view.setViewRepliesOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.debug(this.getClass(),"View");
                        repliesListener.itemSelected(item);
                    }
                });
            }


            view.getImageView().setVisibility(View.VISIBLE);
            view.setLoading(false);

            view.setTag(Integer.valueOf(position));

            Utils.loadBitmap(item.getMediaId(),view.getImageView(),true,context.getResources());

            return view;
        }
        return convertView;

    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Item item){
        items.add(item);
        this.notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
        this.notifyDataSetChanged();
    }

}
