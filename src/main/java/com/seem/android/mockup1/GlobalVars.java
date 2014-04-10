package com.seem.android.mockup1;

import android.os.Environment;
import android.view.ViewGroup;

/**
 * Created by igbopie on 13/03/14.
 */
public class GlobalVars {


    public enum PhotoSource{CAMERA,GALLERY};


    public static final String SAVED_BUNDLE_CURRENT_ITEM_ID = "currentItemId";
    public static final String SAVED_BUNDLE_CAMERASTARTED ="cameraStarted";
    public static final String SAVED_BUNDLE_CAMERA_OUT_FILE ="cameraOutFile";

    public static final String EXTRA_SEEM_ID = "seemId";

    public static final String EXTRA_IS_MAIN_ITEM = "isMainItem";
    public static final String EXTRA_ITEM_ID = "replyId";
    public static final String EXTRA_DEPTH = "depth";
    public static final String EXTRA_PHOTO_SOURCE = "photoSource";
    public static final String EXTRA_USERNAME= "username";

    public static final String EXTRA_CURRENT_ITEM_ID = "currentItemId";
    public static final String EXTRA_PARENT_ITEM_ID = "parentItemId";

    public static final int RETURN_CODE_TAKE_PHOTO = 872;
    public static final int RETURN_CODE_ITEM_FULLSCREEN = 873;
    public static final int RETURN_CODE_CREATE_SEEM = 874;
    public static final int RETURN_CODE_REPLY_TO_ITEM = 875;
    public static final int RETURN_CODE_GALLERY = 876;

    public static final String SHARED_PREF = "myprefe";
    public static final String SHARED_PREF_USERNAME = "username";
    public static final String SHARED_PREF_PASSWORD = "password";
    public static final String SHARED_PREF_TOKEN = "token";
    public static final String SHARED_PREF_AUTHENTICATED = "authenticated";


    public static final String APP_NAME = "SeemMockup1";
    public static final String DIRECTORY_PICTURES = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"+APP_NAME+"/";
    //public static final String INTERNAL_DIRECTORY_PICTURES = Environment.getgetExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"+APP_NAME+"/";

    public static int GRID_SIZE;

    public static int GRID_NUMBER_OF_PHOTOS = 4;
}
