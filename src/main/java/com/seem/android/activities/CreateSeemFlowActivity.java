package com.seem.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.seem.android.model.Seem;
import com.seem.android.service.SeemService;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.io.FileNotFoundException;

/**
 * Created by igbopie on 21/03/14.
 */
public class CreateSeemFlowActivity extends Activity {


    String title;
    String caption;
    ImageView imageView;
    EditText editText;
    EditText titleText;
    Button submit;
    Uri localTempFile;
    Bitmap localBitmap;
    Boolean cameraStarted = false;

    GlobalVars.PhotoSource source;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seem_flow);

        imageView = (ImageView) findViewById(R.id.previewImageView);
        editText = (EditText) findViewById(R.id.captionEditText);
        submit = (Button) findViewById(R.id.seemItButton);
        titleText = (EditText) findViewById(R.id.seemTitleEditText);

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
                caption = editText.getText().toString();
                title = titleText.getText().toString();
                new UploadMedia().execute();
            }
        });
        //---

        source = GlobalVars.PhotoSource.valueOf(getIntent().getStringExtra(GlobalVars.EXTRA_PHOTO_SOURCE));

        if(savedInstanceState != null && savedInstanceState.containsKey(GlobalVars.SAVED_BUNDLE_CAMERASTARTED)) {
            Utils.debug(this.getClass(),"Camera Value recovered");
            cameraStarted = savedInstanceState.getBoolean(GlobalVars.SAVED_BUNDLE_CAMERASTARTED);
            if(savedInstanceState.containsKey(GlobalVars.SAVED_BUNDLE_CAMERA_OUT_FILE)) {
                localTempFile = Uri.parse(savedInstanceState.getString(GlobalVars.SAVED_BUNDLE_CAMERA_OUT_FILE));
            }
        }

        if(!cameraStarted) {
            cameraStarted = true;
            if(source.equals(GlobalVars.PhotoSource.CAMERA)) {
                localTempFile = Utils.getNewFileUri();
                ActivityFactory.startCamera(this, localTempFile);
            }else{
                ActivityFactory.startGallery(this);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug(this.getClass(),"Create Seem Flow Activity OnActivityResult");
        if (requestCode == GlobalVars.RETURN_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Utils.debug(this.getClass(),"Create Seem Flow Activity - Pic taken");
            localBitmap = Utils.shrinkBitmap(localTempFile.getPath());
            imageView.setImageBitmap(localBitmap);
        } else if(requestCode == GlobalVars.RETURN_CODE_GALLERY && resultCode == Activity.RESULT_OK){
            //localTempFile = ;

            try {
                imageView.setImageBitmap(
                        Utils.shrinkBitmapFromStream(
                                getContentResolver().openInputStream(data.getData()),
                                getContentResolver().openInputStream(data.getData()))
                );
            } catch (FileNotFoundException e) {
                Utils.debug(getClass(),"Error "+e);
            }
        } else {
            Utils.debug(this.getClass(),"Create Seem Flow Activity - Pic Cancelled");
            ActivityFactory.finishActivity(this,Activity.RESULT_CANCELED);
        }

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utils.debug(this.getClass(),"Create Seem Flow - onSaveInstanceState");
        outState.putBoolean(GlobalVars.SAVED_BUNDLE_CAMERASTARTED,cameraStarted);
        if(localTempFile != null) {
            outState.putString(GlobalVars.SAVED_BUNDLE_CAMERA_OUT_FILE, localTempFile.getPath());
        }
    }

    private class UploadMedia extends AsyncTask<Void,Void,Seem> {
        private final ProgressDialog dialog = new ProgressDialog(CreateSeemFlowActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Uploading...");
            dialog.show();
        }

        @Override
        protected Seem doInBackground(Void... items) {

            try {
                String mediaId = Api.createMedia(getContentResolver().openInputStream(localTempFile));
                if(mediaId != null){
                    return SeemService.getInstance().save(title, caption, mediaId);
                }else {
                    Utils.debug(this.getClass(),"Error uploading");
                }
            }catch (Exception e) {
                Utils.debug(this.getClass(),"Pete al crear la imagen",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Seem item) {
            super.onPostExecute(item);
            dialog.dismiss();

            ActivityFactory.finishActivity(CreateSeemFlowActivity.this, Activity.RESULT_OK);
        }
    }

}
