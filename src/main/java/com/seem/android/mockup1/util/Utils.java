package com.seem.android.mockup1.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.seem.android.mockup1.GlobalVars;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by igbopie on 13/03/14.
 */
public class Utils {

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
                Utils.debug("ERROR bitmap is null!! wrong file or what? : "+file);
            }
            //int squareSize = Math.min(bitmap.getWidth(), bitmap.getHeight());

            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);

            Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            return croppedBmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void initApp(){
        createBaseDirs();
    }

    public static void debug(String msg){
        Log.d(GlobalVars.APP_NAME,msg);
    }
    public static void debug(String msg,Exception e){
        Log.d(GlobalVars.APP_NAME,msg,e);
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
            Utils.debug("Error creating file " + file + " :", e);
        }
        return Uri.fromFile(newfile);

    }





}
