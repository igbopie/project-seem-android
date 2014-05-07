package com.seem.android.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.seem.android.asynctask.DownloadAsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by igbopie on 07/05/14.
 */
public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<DownloadAsyncTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap,DownloadAsyncTask bitmapWorkerTask) {
        super(res, bitmap);
        bitmapWorkerTaskReference =
                new WeakReference<DownloadAsyncTask>(bitmapWorkerTask);
    }

    public DownloadAsyncTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}