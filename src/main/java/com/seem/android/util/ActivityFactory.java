package com.seem.android.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.seem.android.GlobalVars;
import com.seem.android.activities.CreateSeemFlowActivity;
import com.seem.android.activities.MainActivity;
import com.seem.android.activities.SeemAddItemFlowActivity;
import com.seem.android.fragments.UserProfileFragment;

import java.util.Map;

/**
 * Created by igbopie on 25/03/14.
 */
public class ActivityFactory {


    public static void startUserProfileActivity(Fragment context,String username) {
        Intent intent = new Intent(context.getActivity(),UserProfileFragment.class);
        intent.putExtra(GlobalVars.EXTRA_USERNAME, username);
        context.startActivity(intent);
    }

    public static void startSeemAddItemActivity(Activity context, String seemId, GlobalVars.PhotoSource photoSource) {
        Intent intent = new Intent(context, SeemAddItemFlowActivity.class);
        intent.putExtra(GlobalVars.EXTRA_SEEM_ID, seemId);
        intent.putExtra(GlobalVars.EXTRA_PHOTO_SOURCE,photoSource.toString());
        context.startActivityForResult(intent, GlobalVars.RETURN_CODE_REPLY_TO_ITEM);
    }

    public static void startCreateSeemActivity(Activity context){
        Intent intent = new Intent(context, CreateSeemFlowActivity.class);
        context.startActivityForResult(intent, GlobalVars.RETURN_CODE_CREATE_SEEM);
    }




    public static void startCamera(Activity activity,Uri returnPhoto){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, returnPhoto);
        activity.startActivityForResult(cameraIntent, GlobalVars.RETURN_CODE_TAKE_PHOTO);
    }
    public static void startCamera(Fragment fragment,Uri returnPhoto){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, returnPhoto);
        fragment.startActivityForResult(cameraIntent, GlobalVars.RETURN_CODE_TAKE_PHOTO);
    }

    public static void startGallery(Activity activity){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true); //TODO API>=honeycomb
        activity.startActivityForResult(Intent.createChooser(intent,"Select Picture"), GlobalVars.RETURN_CODE_GALLERY);
    }

    public static void startGallery(Fragment fragment){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true); //TODO API>=honeycomb
        fragment.startActivityForResult(Intent.createChooser(intent,"Select Picture"), GlobalVars.RETURN_CODE_GALLERY);
    }


    public static void startMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    public static void finishActivity(Activity activity,int result){
        Intent data = new Intent();
        activity.setResult(result, data);
        //android:noHistory = "true"
        /*if (activity.getParent() == null) {
            activity.setResult(result, data);
        } else {
            activity.getParent().setResult(result, data);
        }*/
        activity.finish();
    }

    public static void finishActivityWithData(Activity activity,Map<String,String> mapData,int result){
        Intent data = new Intent();
        for(Map.Entry<String,String> entry:mapData.entrySet()) {
            data.putExtra(entry.getKey(),entry.getValue());
        }
        activity.setResult(result, data);
        //android:noHistory = "true"
        /*if (activity.getParent() == null) {
            activity.setResult(result, data);
        } else {
            activity.getParent().setResult(result, data);
        }*/
        activity.finish();
    }
}
