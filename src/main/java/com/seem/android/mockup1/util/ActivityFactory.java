package com.seem.android.mockup1.util;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.activities.CreateSeemFlowActivity;
import com.seem.android.mockup1.activities.ItemActivity;
import com.seem.android.mockup1.activities.ItemsFullScreenActivity;
import com.seem.android.mockup1.activities.ReplyFlowActivity;
import com.seem.android.mockup1.activities.ThreadedViewActivity;
import com.seem.android.mockup1.adapters.ThreadedAdapter;
import com.seem.android.mockup1.model.Item;

/**
 * Created by igbopie on 25/03/14.
 */
public class ActivityFactory {



    public static void startReplyItemActivity(Fragment context,String itemId) {
        Intent intent = new Intent(context.getActivity(), ReplyFlowActivity.class);
        intent.putExtra(GlobalVars.EXTRA_ITEM_ID, itemId);
        context.startActivityForResult(intent, GlobalVars.RETURN_CODE_TAKE_PHOTO);
    }
    public static void startReplyItemActivity(Activity context,String itemId) {
        Intent intent = new Intent(context, ReplyFlowActivity.class);
        intent.putExtra(GlobalVars.EXTRA_ITEM_ID, itemId);
        context.startActivityForResult(intent, GlobalVars.RETURN_CODE_REPLY_TO_ITEM);
    }

    public static void startCreateSeemActivity(Activity context){
        Intent intent = new Intent(context, CreateSeemFlowActivity.class);
        context.startActivityForResult(intent, GlobalVars.RETURN_CODE_CREATE_SEEM);
    }

    public static void startItemFullscreenActivity(Activity context, String seemId, String parentItem, String currentItem){
        Intent intent = new Intent(context, ItemsFullScreenActivity.class);
        intent.putExtra(GlobalVars.EXTRA_SEEM_ID,seemId);
        intent.putExtra(GlobalVars.EXTRA_CURRENT_ITEM_ID,currentItem);
        intent.putExtra(GlobalVars.EXTRA_PARENT_ITEM_ID,parentItem);
        context.startActivity(intent);

    }

    public static void startItemActivity(Activity activity, String seemId, String itemId){
        Intent intent = new Intent(activity, ItemActivity.class);
        intent.putExtra(GlobalVars.EXTRA_SEEM_ID,seemId);
        intent.putExtra(GlobalVars.EXTRA_ITEM_ID,itemId);
        activity.startActivity(intent);
    }

    public static void startCamera(Activity activity,Uri returnPhoto){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, returnPhoto);
        activity.startActivityForResult(cameraIntent, GlobalVars.RETURN_CODE_TAKE_PHOTO);
    }


    public static void startThreadedActivity(Activity activity,String itemId){
        Intent intent = new Intent(activity, ThreadedViewActivity.class);
        intent.putExtra(GlobalVars.EXTRA_ITEM_ID, itemId);
        activity.startActivity(intent);
    }

    public static void finishActivity(Activity activity,int result){
        //android:noHistory = "true"
        Intent data = new Intent();
        if (activity.getParent() == null) {
            activity.setResult(result, data);
        } else {
            activity.getParent().setResult(result, data);
        }
        activity.finish();
    }
}
