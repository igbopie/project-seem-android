package com.seem.android.mockup1;

import android.os.Environment;

/**
 * Created by igbopie on 13/03/14.
 */
public class GlobalVars {

    public static final String EXTRA_REPLY_ID = "replyId";
    public static final String EXTRA_DEPTH = "depth";

    public static final int TAKE_PHOTO_CODE = 872;

    public static final String APP_NAME = "SeemMockup1";
    public static final String DIRECTORY_PICTURES = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"+APP_NAME+"/";

}
