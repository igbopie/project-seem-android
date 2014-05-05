package com.seem.android.adapters;

import android.content.Context;
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
import com.seem.android.model.Seem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 18/03/14.
 */
public class SeemAdapter extends ArrayAdapter<Seem> {

    private List<Seem> itemList;
    private Context context;
    private Map<ImageView,DownloadAsyncTask> imageViewProcesses = new HashMap<ImageView, DownloadAsyncTask>();

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

        if(imageViewProcesses.containsKey(mainImageView.getImageView())){
            imageViewProcesses.get(mainImageView.getImageView()).cancel(true);
        }

        mainImageView.getImageView().setImageBitmap(null);
        DownloadAsyncTask mainDTask = new DownloadAsyncTask(c.getItemMediaId(),mainImageView.getImageView(),true);
        imageViewProcesses.put(mainImageView.getImageView(),mainDTask);
        mainDTask.execute();

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

        miniView1.getImageView().setImageBitmap(null);
        miniView2.getImageView().setImageBitmap(null);
        miniView3.getImageView().setImageBitmap(null);
        miniView4.getImageView().setImageBitmap(null);
        miniView5.getImageView().setImageBitmap(null);

        if(imageViewProcesses.containsKey(miniView1.getImageView())){
            imageViewProcesses.get(miniView1.getImageView()).cancel(true);
        }

        if(imageViewProcesses.containsKey(miniView2.getImageView())){
            imageViewProcesses.get(miniView2.getImageView()).cancel(true);
        }

        if(imageViewProcesses.containsKey(miniView3.getImageView())){
            imageViewProcesses.get(miniView3.getImageView()).cancel(true);
        }

        if(imageViewProcesses.containsKey(miniView4.getImageView())){
            imageViewProcesses.get(miniView4.getImageView()).cancel(true);
        }

        if(imageViewProcesses.containsKey(miniView5.getImageView())){
            imageViewProcesses.get(miniView5.getImageView()).cancel(true);
        }

        switch (c.getLastestItems().size()){
            case 5:
                miniView5.setLoading(false);
                DownloadAsyncTask miniTask = new DownloadAsyncTask(c.getLastestItems().get(4),miniView5.getImageView(),true);
                miniTask.execute();
                imageViewProcesses.put(miniView5.getImageView(),miniTask);
                miniView5.setVisibility(View.VISIBLE);
            case 4:
                miniView4.setLoading(false);
                miniTask = new DownloadAsyncTask(c.getLastestItems().get(3),miniView4.getImageView(),true);
                miniTask.execute();
                imageViewProcesses.put(miniView4.getImageView(),miniTask);
                miniView4.setVisibility(View.VISIBLE);
            case 3:
                miniView3.setLoading(false);
                miniTask = new DownloadAsyncTask(c.getLastestItems().get(2),miniView3.getImageView(),true);
                miniTask.execute();
                imageViewProcesses.put(miniView3.getImageView(),miniTask);
                miniView3.setVisibility(View.VISIBLE);
            case 2:
                miniView2.setLoading(false);
                miniTask = new DownloadAsyncTask(c.getLastestItems().get(1),miniView2.getImageView(),true);
                miniTask.execute();
                imageViewProcesses.put(miniView2.getImageView(),miniTask);
                miniView2.setVisibility(View.VISIBLE);
            case 1:
                miniView1.setLoading(false);
                miniTask = new DownloadAsyncTask(c.getLastestItems().get(0),miniView1.getImageView(),true);
                miniTask.execute();
                imageViewProcesses.put(miniView1.getImageView(),miniTask);
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