package com.seem.android.mockup1;

import android.os.Environment;
import android.view.ViewGroup;

/**
 * Created by igbopie on 13/03/14.
 */
public class GlobalVars {


    public static final String EXTRA_SEEM_ID = "seemId";

    public static final String EXTRA_ITEM_ID = "replyId";
    public static final String EXTRA_DEPTH = "depth";

    public static final int TAKE_PHOTO_CODE = 872;

    public static final String APP_NAME = "SeemMockup1";
    public static final String DIRECTORY_PICTURES = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"+APP_NAME+"/";

    public static ViewGroup.LayoutParams layoutParamsForSmallReplies;

    public static int GRID_NUMBER_OF_PHOTOS = 2;
}
