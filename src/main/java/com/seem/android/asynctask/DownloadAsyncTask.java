package com.seem.android.asynctask;

import android.os.AsyncTask;
import android.widget.ImageView;

import com.seem.android.model.Item;
import com.seem.android.model.Media;
import com.seem.android.service.MediaService;

/**
 * Created by igbopie on 17/04/14.
 */
public class DownloadAsyncTask extends AsyncTask<Void,Void,Void> {

    Media media;
    Item item;
    ImageView imageView;
    boolean thumb;


    public DownloadAsyncTask(Item item, ImageView imageView,boolean thumb) {
        this.media = new Media(item.getMediaId());
        this.item = item;
        this.imageView = imageView;
        this.thumb=thumb;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(thumb) {
            MediaService.getInstance().getThumb(this.media);
        }else{
            MediaService.getInstance().getLarge(this.media);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        if (this.isCancelled()) {
            //well... do not paint...
        } else if (item != null) {
            if(thumb){
                imageView.setImageDrawable(this.media.getImageThumb());
            }else{
                imageView.setImageDrawable(this.media.getImageLarge());
            }

        }

        this.item = null;
        this.media = null;
        this.imageView = null;

    }
}
