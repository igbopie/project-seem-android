package com.seem.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.seem.android.GlobalVars;
import com.seem.android.customviews.SpinnerImageView;
import com.seem.android.executor.AsyncExecutor;
import com.seem.android.executor.MyAsyncTask;
import com.seem.android.model.Item;
import com.seem.android.model.Media;
import com.seem.android.service.MediaService;
import com.seem.android.util.ItemSelectedListener;
import com.seem.android.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 24/03/14.
 */
public class ThumbnailAdapterV2 extends BaseAdapter {

    Context context;
    List<Item> items = new ArrayList<Item>();

    Map<View,FetchThumbs> execViewMap = new HashMap<View, FetchThumbs>();
    ItemSelectedListener repliesListener;
    ItemSelectedListener threadViewListener;
    public ThumbnailAdapterV2(Context context, ItemSelectedListener repliesListener, ItemSelectedListener threadViewListener) {
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
            //If we are not redrawing the same thing

            Item item = (Item) getItem(position);

            view.setText(item.getCaption());
            addProcess(view, item);

            view.setTag(Integer.valueOf( position));
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

    private void addProcess(SpinnerImageView view,Item item){
        //be careful with reusing views...

        FetchThumbs ft = new FetchThumbs(view, item);
        AsyncExecutor.getInstance().add(ft);


        FetchThumbs oldExec = execViewMap.get(view);
        execViewMap.put(view, ft);

        if (oldExec != null) {
            AsyncExecutor.getInstance().cancelTask(oldExec);
        }

    }



    private class FetchThumbs extends MyAsyncTask {
        private SpinnerImageView imageView;
        private Item item;
        private Media media;

        public FetchThumbs(SpinnerImageView imageView,Item item){
            this.imageView = imageView;
            this.item = item;
            this.media = new Media(item.getMediaId());

        }

        @Override
        protected Void doInBackground(Void... voids) {
            MediaService.getInstance().getThumb(this.media);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(this.isCancelled()){
                //well... do not paint...
            }else if(item != null) {
                imageView.getImageView().setImageDrawable(this.media.getImageThumb());
                imageView.setRepliesNumber(item.getReplyCount());
                if(item.getReplyCount() > 0) {
                    imageView.setViewRepliesOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.debug(this.getClass(),"View");
                            repliesListener.itemSelected(item);
                        }
                    });
                }
                /*
                if(item.getDepth() > 0 ){
                    imageView.setDepthNumber(item.getDepth());
                    imageView.setViewThreadOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            threadViewListener.itemSelected(item);
                        }
                    });
                }*/

                imageView.getImageView().setVisibility(View.VISIBLE);
                imageView.setLoading(false);
            }
        }
    }
}
