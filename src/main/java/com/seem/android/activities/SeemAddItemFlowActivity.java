package com.seem.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.seem.android.service.Api;
import com.seem.android.GlobalVars;
import com.seem.android.R;
import com.seem.android.model.Item;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.util.Calendar;

/**
 * Created by igbopie on 21/03/14.
 */
public class SeemAddItemFlowActivity extends Activity {

    Item itemInProgress;
    ImageView imageView;
    EditText editText;
    Button submit;
    String seemId;
    boolean cameraStarted;



    GlobalVars.PhotoSource source;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.debug(this.getClass(),"ReplyFlow OnCreate");
        setContentView(R.layout.activity_reply_flow);
        seemId = getIntent().getStringExtra(GlobalVars.EXTRA_SEEM_ID);

        imageView = (ImageView) findViewById(R.id.previewImageView);
        editText = (EditText) findViewById(R.id.captionEditText);
        submit = (Button) findViewById(R.id.seemItButton);

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit.performClick();
                    return true;
                }
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemInProgress.setCaption(editText.getText().toString());
                itemInProgress.setCreated(Calendar.getInstance().getTime());
                new UploadMedia().execute(itemInProgress);
            }
        });

        source = GlobalVars.PhotoSource.valueOf(getIntent().getStringExtra(GlobalVars.EXTRA_PHOTO_SOURCE));
        //---
        if(savedInstanceState != null && savedInstanceState.containsKey(GlobalVars.SAVED_BUNDLE_CAMERASTARTED)) {
            Utils.debug(this.getClass(),"Reply Flow - Camera Value recovered");
            cameraStarted = savedInstanceState.getBoolean(GlobalVars.SAVED_BUNDLE_CAMERASTARTED);
            itemInProgress = new Item();
            if(savedInstanceState.containsKey(GlobalVars.SAVED_BUNDLE_CAMERA_OUT_FILE)) {
                itemInProgress.setTempLocalFile(Uri.parse(savedInstanceState.getString(GlobalVars.SAVED_BUNDLE_CAMERA_OUT_FILE)));
            }
        }

        if(!cameraStarted) {
            cameraStarted = true;
            itemInProgress = new Item();
            if(source.equals(GlobalVars.PhotoSource.CAMERA)) {
                itemInProgress.setTempLocalFile(Utils.getNewFileUri());
                ActivityFactory.startCamera(this, itemInProgress.getTempLocalFile());
            }else{
                ActivityFactory.startGallery(this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utils.debug(this.getClass(),"Reply Flow - onSaveInstanceState");
        outState.putBoolean(GlobalVars.SAVED_BUNDLE_CAMERASTARTED,cameraStarted);
        if(itemInProgress != null && itemInProgress.getTempLocalFile() != null) {
            outState.putString(GlobalVars.SAVED_BUNDLE_CAMERA_OUT_FILE, itemInProgress.getTempLocalFile().getPath());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.RETURN_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //DO nothing
        }  else if(requestCode == GlobalVars.RETURN_CODE_GALLERY && resultCode == Activity.RESULT_OK){
            itemInProgress.setTempLocalFile(data.getData());
        } else {
            Utils.debug(this.getClass(),"Reply Flow Activity - Pic Cancelled");
            ActivityFactory.finishActivity(this,Activity.RESULT_CANCELED);
        }

        if(resultCode == Activity.RESULT_OK){
            Utils.loadStream(itemInProgress.getTempLocalFile(),imageView,this);
        }

    }

    private class UploadMedia extends AsyncTask<Item,Void,Item> {
        private final ProgressDialog dialog = new ProgressDialog(SeemAddItemFlowActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Uploading...");
            dialog.show();
        }

        @Override
        protected Item doInBackground(Item... items) {

            try {
                String mediaId = Api.createMedia(getContentResolver().openInputStream(items[0].getTempLocalFile()));
                if(mediaId != null){
                    items[0].setMediaId(mediaId);
                    return Api.addToSeem(items[0].getCaption(),items[0].getMediaId(),seemId);
                }else {
                    Utils.debug(this.getClass(),"Error uploading");
                }
            }catch (Exception e) {
                Utils.debug(this.getClass(),"Pete al crear la imagen",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Item item) {
            super.onPostExecute(item);
            dialog.dismiss();
            ActivityFactory.finishActivity(SeemAddItemFlowActivity.this,Activity.RESULT_OK);
        }
    }

}
