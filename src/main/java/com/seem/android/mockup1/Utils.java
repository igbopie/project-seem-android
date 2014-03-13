package com.seem.android.mockup1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by igbopie on 13/03/14.
 */
public class Utils {

    public static Bitmap shrinkBitmap(String file){

        //500x500

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

        bmpFactoryOptions.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        boolean width = bmpFactoryOptions.outWidth > bmpFactoryOptions.outHeight;


        int ratio = 0;
        if(width) {
            ratio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) 500);
        } else {
            ratio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) 500);
        }
        bmpFactoryOptions.inSampleSize = ratio;


        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int squareSize = Math.min(bitmap.getHeight(), bitmap.getHeight());

        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,squareSize,squareSize,matrix,true);

        return croppedBmp;
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

    public static Uri getNewFileUri(int id){
        createBaseDirs();

        String file = GlobalVars.DIRECTORY_PICTURES+id+".jpg";

        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            Utils.debug("Error creating file " + file + " :", e);
        }
        return Uri.fromFile(newfile);

    }
}
