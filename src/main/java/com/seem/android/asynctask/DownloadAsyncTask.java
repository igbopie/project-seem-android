package com.seem.android.asynctask;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.seem.android.model.Item;
import com.seem.android.model.Media;
import com.seem.android.service.MediaService;

import java.lang.ref.WeakReference;

/**
 * Created by igbopie on 17/04/14.
 */
public class DownloadAsyncTask extends AsyncTask<Void,Void,Bitmap> {

    private Media media;
    private WeakReference<ImageView> imageViewReference;
    private boolean thumb;


    public DownloadAsyncTask(Item item, ImageView imageView,boolean thumb) {
        this.media = new Media(item.getMediaId());
        this.imageViewReference = new WeakReference<ImageView>(imageView);
        this.thumb=thumb;
    }

    public DownloadAsyncTask(String mediaId, ImageView imageView,boolean thumb) {
        this.media = new Media(mediaId);
        this.imageViewReference = new WeakReference<ImageView>(imageView);
        this.thumb=thumb;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        if(thumb) {
            return MediaService.getInstance().getThumb(this.media);
        }else{
            return MediaService.getInstance().getLarge(this.media);
        }
    }

    @Override
    protected void onPostExecute(Bitmap v) {
        super.onPostExecute(v);
        if (imageViewReference != null) {
            final ImageView imageView = imageViewReference.get();
            if(imageView!= null) {
                if (this.isCancelled()) {
                    //well... do not paint...
                    imageView.setImageDrawable(null);
                } else if (media != null) {
                    imageView.setImageBitmap(v);
                }
            }
        }
        this.media = null;
    }

    public Media getMedia() {
        return media;
    }
}
