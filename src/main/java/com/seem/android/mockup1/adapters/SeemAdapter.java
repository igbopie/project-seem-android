package com.seem.android.mockup1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seem.android.mockup1.R;
import com.seem.android.mockup1.model.Seem;

import java.util.List;

/**
 * Created by igbopie on 18/03/14.
 */
public class SeemAdapter extends ArrayAdapter<Seem> {

    private List<Seem> itemList;
    private Context context;

    public SeemAdapter(List<Seem> itemList, Context ctx) {
        super(ctx, R.layout.seem_list_item, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public Seem getItem(int position) {
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
            v = inflater.inflate(R.layout.seem_list_item, null);
        }

        Seem c = itemList.get(position);
        TextView text = (TextView) v.findViewById(R.id.text_view);
        text.setText(c.getTitle());

        return v;

    }

    public List<Seem> getItemList() {
        return itemList;
    }

    public void setItemList(List<Seem> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }




}