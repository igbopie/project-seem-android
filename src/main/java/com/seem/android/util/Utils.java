package com.seem.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.seem.android.GlobalVars;
import com.seem.android.service.Api;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by igbopie on 13/03/14.
 */
public class Utils {

    public static float dpToPixel(float amount,Context context){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, amount, context.getResources().getDisplayMetrics());
    }
    public static void loadBitmap(String mediaId,Api.ImageFormat format,ImageView imageView,Context context) {
        Picasso.with(context).load(Api.getImageEndpoint(mediaId,format)).into(imageView);
    }

    public static void loadBitmap(String mediaId,Api.ImageFormat format,ImageView imageView,Context context,Callback callback) {
        Picasso.with(context).load(Api.getImageEndpoint(mediaId,format)).into(imageView,callback);
    }

    public static void loadStream(Uri path,ImageView imageview,Context context){
        Picasso.with(context).load(path).resize(GlobalVars.SCREEN_WIDTH, GlobalVars.SCREEN_HEIGHT).centerInside().into(imageview);
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
