package com.seem.android.mockup1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seem.android.mockup1.asynctask.DownloadAsyncTask;
import com.seem.android.mockup1.R;

import com.seem.android.mockup1.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 18/03/14.
 */
public class ThreadedAdapter extends ArrayAdapter<Item> {

    private List<Item> itemList;
    private Context context;
    private Map<View,DownloadAsyncTask> taskMap = new HashMap<View, DownloadAsyncTask>();

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

        if(convertView != null){
            DownloadAsyncTask dat = taskMap.get(convertView);
            if(dat != null){
                dat.cancel(true);
                taskMap.put(convertView,null);
            }
        }

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.component_threaded_list, null);
        }

        Item c = itemList.get(position);
        ImageView imageView = (ImageView) v.findViewById(R.id.threadImageView);

        TextView depthTextView = (TextView) v.findViewById(R.id.depthTextView);
        depthTextView.setText(c.getDepth()+"");

        TextView captionTextView = (TextView) v.findViewById(R.id.captionTextView);
        captionTextView.setText(c.getCaption());


        DownloadAsyncTask dat = new DownloadAsyncTask(c,imageView,true);
        dat.execute();
        taskMap.put(convertView,dat);


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