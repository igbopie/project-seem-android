package com.seem.android.uimodel;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.service.Api;
import com.seem.android.util.Utils;

import java.util.ArrayList;

/**
 * Created by igbopie on 03/04/14.
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }


        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        NavDrawerItem dItem = navDrawerItems.get(position);

        if(dItem.getItem() != null){
            txtTitle.setText(dItem.getItem().getCaption());
            Utils.loadBitmap(dItem.getItem().getMediaId(), Api.ImageFormat.THUMB, imgIcon, context);
        }else {
            if(dItem.isSectionTitle()) {
                imgIcon.setVisibility(View.INVISIBLE);
            }else{
                imgIcon.setImageResource(dItem.getIcon());
            }
            txtTitle.setText(dItem.getTitle());
        }

        if(dItem.isSectionTitle()){
            convertView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.SeemDarkBlue));
            txtTitle.setGravity(Gravity.CENTER);
        }


        // displaying count
        // check whether it set visible or not
        if(navDrawerItems.get(position).getCounterVisibility()){
            txtCount.setText(navDrawerItems.get(position).getCount());
        }else{
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled () {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return !navDrawerItems.get(position).isSectionTitle();
    }

}