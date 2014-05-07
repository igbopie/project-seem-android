package com.seem.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.asynctask.DownloadAsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by igbopie on 13/03/14.
 */
public class Utils {

    private static DownloadAsyncTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static boolean cancelPotentialWork(String mediaId, ImageView imageView) {
        final DownloadAsyncTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String taskMediaId = bitmapWorkerTask.getMedia().getId();
            // If bitmapData is not yet set or it differs from the new data
            if (taskMediaId == null || !taskMediaId.equals(mediaId)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public static void loadBitmap(String mediaId,ImageView imageView,boolean thumb,Resources res) {
        if (cancelPotentialWork(mediaId, imageView)) {
            Bitmap mLoadingBitmap = null;
            final DownloadAsyncTask task = new DownloadAsyncTask(mediaId,imageView,thumb);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(res,mLoadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute();
        }
    }

    public static Bitmap fromFile(String file) throws IOException {
        //System.gc();
        ExifInterface exif  = new ExifInterface(file);;
        int rotate = 0;

        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        int ratio = 0;
        boolean width = bmpFactoryOptions.outWidth > bmpFactoryOptions.outHeight;
        if(width) {
            ratio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) GlobalVars.MAX_WIDTH);
        } else {
            ratio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) GlobalVars.MAX_WIDTH);
        }
        bmpFactoryOptions.inSampleSize = ratio;
        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        BitmapDrawable br =  new BitmapDrawable(MyApplication.getAppContext().getResources(),croppedBmp);
        bitmap = null;
        croppedBmp = null;
        bmpFactoryOptions = null;
        matrix = null;
        return br.getBitmap();
    }

    /**
     * We need it twice. One for check the size, and the other one to really read it.
     * @param stream
     * @param clonStream
     * @return
     */
    public static Bitmap shrinkBitmapFromStream(InputStream stream,InputStream clonStream){
        System.gc();
        try {
            //500x500
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, null, bmpFactoryOptions);
            boolean width = bmpFactoryOptions.outWidth > bmpFactoryOptions.outHeight;
            int ratio = 0;
            if(width) {
                ratio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) GlobalVars.MAX_WIDTH);
            } else {
                ratio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) GlobalVars.MAX_WIDTH);
            }
            Utils.debug(Utils.class,"Bitmap size "+bmpFactoryOptions.outWidth +"x"+ bmpFactoryOptions.outHeight+" Ratio:"+ratio);
            bmpFactoryOptions.inSampleSize = ratio;

            bmpFactoryOptions.inJustDecodeBounds = false;

            //stream.reset();

            Bitmap bitmap =  BitmapFactory.decodeStream(clonStream, null, bmpFactoryOptions);
            if(bitmap == null){
                Utils.debug(Utils.class,"ERROR bitmap is null!! wrong file or what?");
            }
            Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(),new Matrix(),true);

            return croppedBmp;
        } catch (Exception e) {
            Utils.debug(Utils.class,"Error shrinking the photo :",e);
        }
        return null;
    }


    public static Bitmap shrinkBitmap(String file){
        System.gc();
        try {
            ExifInterface exif  = new ExifInterface(file);;
            int rotate = 0;

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            //500x500
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file, bmpFactoryOptions);
            boolean width = bmpFactoryOptions.outWidth > bmpFactoryOptions.outHeight;
            int ratio = 0;
            if(width) {
                ratio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) GlobalVars.MAX_WIDTH);
            } else {
                ratio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) GlobalVars.MAX_WIDTH);
            }
            bmpFactoryOptions.inSampleSize = ratio;

            bmpFactoryOptions.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
            if(bitmap == null){
                Utils.debug(Utils.class,"ERROR bitmap is null!! wrong file or what? : "+file);
            }
            //int squareSize = Math.min(bitmap.getWidth(), bitmap.getHeight());

            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);

            Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            return croppedBmp;
        } catch (Exception e) {
            Utils.debug(Utils.class,"Error shrinking the photo: "+file);
        }
        return null;
    }

    public static void initApp(){
        createBaseDirs();
    }

    public static void debug(Class clazz,String msg){
        Log.d(GlobalVars.APP_NAME,clazz+":"+msg);
    }
    public static void debug(Class clazz,String msg,Exception e){
        Log.d(GlobalVars.APP_NAME,clazz+":"+msg,e);
    }

    private static void createBaseDirs(){
        File newdir = new File(GlobalVars.DIRECTORY_PICTURES);
        boolean success = newdir.mkdirs();

        if( !newdir.exists() && !success ){
            Log.d(GlobalVars.APP_NAME, "Could not create folder...");
            //TODO throw error
        }
    }

    public static Uri getNewFileUri(){
        createBaseDirs();

        String randomId = Calendar.getInstance().getTimeInMillis()+"";
        String file = GlobalVars.DIRECTORY_PICTURES+randomId+".jpg";

        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            Utils.debug(Utils.class,"Error creating file " + file + " :", e);
        }
        return Uri.fromFile(newfile);

    }

    /**
     * TODO will not work with dropbox and others apps
     * @param activity
     * @param uri
     * @return
     */
    public static String getRealPathFromGalleryUri(Activity activity,Uri uri) {

        Utils.debug(Utils.class,"From gallery URI: "+uri);
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }


    public static void dialog(String title,String description,Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(description)
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static CharSequence getRelativeTime(Date date) {
        long epoch = date.getTime();
        return DateUtils.getRelativeTimeSpanString(epoch, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

}
