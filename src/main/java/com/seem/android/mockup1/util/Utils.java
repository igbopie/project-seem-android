package com.seem.android.mockup1.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.MyApplication;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by igbopie on 13/03/14.
 */
public class Utils {

    public static Drawable fromFile(String file) throws IOException {
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
            ratio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) 1000);
        } else {
            ratio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) 1000);
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
        System.gc();
        return br;
    }
    public static Bitmap shrinkBitmap(String file){
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
                ratio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) 1000);
            } else {
                ratio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) 1000);
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


    //UPDATED!
    public static String getRealPathFromGalleryUri(Activity activity,Uri uri) {
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


}
