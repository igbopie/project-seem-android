package com.seem.android.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seem.android.R;
import com.seem.android.asynctask.DownloadAsyncTask;
import com.seem.android.customviews.SpinnerImageView;
import com.seem.android.fragments.SeemListFragment;
import com.seem.android.model.Seem;
import com.seem.android.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 18/03/14.
 */
public class SeemAdapter extends ArrayAdapter<Seem> {

    private List<Seem> itemList;
    private Context context;
    private SeemListFragment.QueryType queryType;

    public SeemAdapter(List<Seem> itemList, Context ctx) {
        super(ctx, R.layout.component_seem_list, itemList);
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
            v = inflater.inflate(R.layout.component_seem_list, null);
        }

        Seem c = itemList.get(position);
        TextView text = (TextView) v.findViewById(R.id.text_view);
        text.setText(c.getTitle());
        TextView itemCount= (TextView)v.findViewById(R.id.seemItemCount);
        itemCount.setText(""+c.getItemCount());

        long epoch = c.getUpdated().getTime();
        TextView updatedText= (TextView)v.findViewById(R.id.updatedText);
        updatedText.setText(DateUtils.getRelativeTimeSpanString(epoch,System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));


        SpinnerImageView mainImageView = (SpinnerImageView) v.findViewById(R.id.mainImageView);
        mainImageView.setText(c.getItemCaption());


        Utils.loadBitmap(c.getItemMediaId(), mainImageView.getImageView(), true, context.getResources());

        mainImageView.setLoading(false);


        SpinnerImageView miniView1 = (SpinnerImageView) v.findViewById(R.id.miniView1);
        SpinnerImageView miniView2 = (SpinnerImageView) v.findViewById(R.id.miniView2);
        SpinnerImageView miniView3 = (SpinnerImageView) v.findViewById(R.id.miniView3);
        SpinnerImageView miniView4 = (SpinnerImageView) v.findViewById(R.id.miniView4);
        SpinnerImageView miniView5 = (SpinnerImageView) v.findViewById(R.id.miniView5);

        miniView5.setVisibility(View.INVISIBLE);
        miniView4.setVisibility(View.INVISIBLE);
        miniView3.setVisibility(View.INVISIBLE);
        miniView2.setVisibility(View.INVISIBLE);
        miniView1.setVisibility(View.INVISIBLE);

        switch (c.getLastestItems().size()){
            case 5:
                miniView5.setLoading(false);
                Utils.loadBitmap(c.getLastestItems().get(4).getMediaId(), miniView5.getImageView(), true, context.getResources());
                miniView5.setVisibility(View.VISIBLE);
            case 4:
                miniView4.setLoading(false);
                Utils.loadBitmap(c.getLastestItems().get(3).getMediaId(), miniView4.getImageView(), true, context.getResources());
                miniView4.setVisibility(View.VISIBLE);
            case 3:
                miniView3.setLoading(false);
                Utils.loadBitmap(c.getLastestItems().get(2).getMediaId(), miniView3.getImageView(), true, context.getResources());
                miniView3.setVisibility(View.VISIBLE);
            case 2:
                miniView2.setLoading(false);
                Utils.loadBitmap(c.getLastestItems().get(1).getMediaId(), miniView2.getImageView(), true, context.getResources());
                miniView2.setVisibility(View.VISIBLE);
            case 1:
                miniView1.setLoading(false);
                Utils.loadBitmap(c.getLastestItems().get(0).getMediaId(), miniView1.getImageView(), true, context.getResources());
                miniView1.setVisibility(View.VISIBLE);
            case 0:
                break;
        }

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