package com.seem.android.service;

import android.content.Context;

import com.seem.android.MyApplication;
import com.seem.android.model.Media;
import com.seem.android.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by igbopie on 02/04/14.
 */
public class MediaService {


    private static final long MAX_SIZE = 10L * 1024L * 1024L; // 5MB

    private static MediaService singleton = null;

    public static synchronized MediaService getInstance(){
        if(singleton == null){
            Utils.debug(ItemService.class, "Creating a new singleton... ");
            singleton = new MediaService(MyApplication.getAppContext());
        }
        return singleton;
    }

    Context context;
    private MediaService(Context context){
        this.context = context;
    }

    public void getThumb(Media media){

        //check in the internal filesystem
        File file = new File(context.getCacheDir(), media.getId()+"_thumb.jpg");
        if(!file.exists()){
            // media.setImageThumb(Drawable.createFromStream(is, media.getId()));
            try {
                //clean cache
                checkCacheSize();
                InputStream is = Api.downloadThumbImage(media);
                //save
                FileOutputStream fos = new FileOutputStream(file);//context.openFileOutput(file.getAbsolutePath(), Context.MODE_PRIVATE);
                copyStream(is,fos);
                fos.close();
            }catch(IOException ex){
                Utils.debug(this.getClass(),ex.getMessage());
            }
        }
        try {
            media.setImageThumb(Utils.fromFile(file.getAbsolutePath()));
        }catch(IOException ex){
            Utils.debug(this.getClass(),ex.getMessage());
        }
    }

    public void getLarge(Media media){

        //check in the internal filesystem
        File file = new File(context.getCacheDir(), media.getId()+"_large.jpg");
        if(!file.exists()){
            // media.setImageThumb(Drawable.createFromStream(is, media.getId()));
            try {
                //clean cache
                checkCacheSize();
                InputStream is = Api.downloadLargeImage(media);
                //save
                FileOutputStream fos = new FileOutputStream(file);//context.openFileOutput(file.getAbsolutePath(), Context.MODE_PRIVATE);
                copyStream(is,fos);
                fos.close();

            }catch(IOException ex){
                Utils.debug(this.getClass(),ex.getMessage());
            }
        }
        try {
            media.setImageLarge(Utils.fromFile(file.getAbsolutePath()));
        }catch(IOException ex){
            Utils.debug(this.getClass(),ex.getMessage());
        }


    }

    private void checkCacheSize(){
        try {
            cleanCache();
        } catch (IOException e) {
            Utils.debug(this.getClass(),"Error cleaning cache"+e);
        }
    }


    private void copyStream(InputStream input, OutputStream output)
            throws IOException
    {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, bytesRead);
        }
    }


    private void cleanCache() throws IOException {

        File cacheDir = context.getCacheDir();
        long size = getDirSize(cacheDir);
        Utils.debug(this.getClass(),"Cache size:"+size);

        if (size > MAX_SIZE) {
            cleanDir(cacheDir, size - MAX_SIZE);
        }

    }


    private void cleanDir(File dir, long bytes) {

        long bytesDeleted = 0;
        File[] files = dir.listFiles();

        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        for (File file : files) {
            bytesDeleted += file.length();
            file.delete();

            if (bytesDeleted >= bytes) {
                break;
            }
        }
    }

    private long getDirSize(File dir) {

        long size = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }

        return size;
    }
}
