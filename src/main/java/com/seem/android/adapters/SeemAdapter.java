package com.seem.android.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seem.android.R;
import com.seem.android.customviews.SquareImageView;
import com.seem.android.fragments.SeemListFragment;
import com.seem.android.model.Seem;
import com.seem.android.service.Api;
import com.seem.android.util.Utils;

import java.util.List;

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

        TextView updatedText= (TextView)v.findViewById(R.id.updatedText);
        updatedText.setText(DateUtils.getRelativeTimeSpanString(c.getExpire().getTime(),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));


        SquareImageView mainImageView = (SquareImageView) v.findViewById(R.id.mainImageView);


        SquareImageView miniView1 = (SquareImageView) v.findViewById(R.id.miniView1);
        SquareImageView miniView2 = (SquareImageView) v.findViewById(R.id.miniView2);
        SquareImageView miniView3 = (SquareImageView) v.findViewById(R.id.miniView3);
        SquareImageView miniView4 = (SquareImageView) v.findViewById(R.id.miniView4);
        SquareImageView miniView5 = (SquareImageView) v.findViewById(R.id.miniView5);

        miniView5.setVisibility(View.INVISIBLE);
        miniView4.setVisibility(View.INVISIBLE);
        miniView3.setVisibility(View.INVISIBLE);
        miniView2.setVisibility(View.INVISIBLE);
        miniView1.setVisibility(View.INVISIBLE);

        switch (c.getLastestItems().size()){
            case 5:
                Utils.loadBitmap(c.getLastestItems().get(4).getMediaId(), Api.ImageFormat.THUMB,miniView4,miniView5.getLayoutParams().width,miniView5.getLayoutParams().width, context);
                miniView4.setVisibility(View.VISIBLE);
            case 4:
                Utils.loadBitmap(c.getLastestItems().get(3).getMediaId(), Api.ImageFormat.THUMB, miniView3,miniView5.getLayoutParams().width,miniView5.getLayoutParams().width, context);
                miniView3.setVisibility(View.VISIBLE);
            case 3:
                Utils.loadBitmap(c.getLastestItems().get(2).getMediaId(), Api.ImageFormat.THUMB, miniView2,miniView5.getLayoutParams().width,miniView5.getLayoutParams().width, context);
                miniView2.setVisibility(View.VISIBLE);
            case 2:
                Utils.loadBitmap(c.getLastestItems().get(1).getMediaId(), Api.ImageFormat.THUMB, miniView1,miniView5.getLayoutParams().width,miniView5.getLayoutParams().width, context);
                miniView1.setVisibility(View.VISIBLE);
            case 1:
                Utils.loadBitmap(c.getLastestItems().get(0).getMediaId(), Api.ImageFormat.THUMB, mainImageView,mainImageView.getLayoutParams().width,mainImageView.getLayoutParams().width, context);

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