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
    ImageView imageView;
    boolean thumb;


    public DownloadAsyncTask(Item item, ImageView imageView,boolean thumb) {
        this.media = new Media(item.getMediaId());
        this.imageView = imageView;
        this.thumb=thumb;
    }

    public DownloadAsyncTask(String mediaId, ImageView imageView,boolean thumb) {
        this.media = new Media(mediaId);
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
            imageView.setImageDrawable(null);
        } else if (media != null) {
            if(thumb){
                imageView.setImageDrawable(this.media.getImageThumb());
            }else{
                imageView.setImageDrawable(this.media.getImageLarge());
            }

        }

        this.media = null;
        this.imageView = null;

    }
}
