package com.seem.android.mockup1.activities;

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

import com.seem.android.mockup1.Api;
import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.Utils;

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
        localTempFile = Utils.getNewFileUri();

        ActivityFactory.startCamera(this,localTempFile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug("Create Seem Flow Activity OnActivityResult");
        if (requestCode == GlobalVars.RETURN_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Utils.debug("Create Seem Flow Activity - Pic taken");
            localBitmap = Utils.shrinkBitmap(localTempFile.getPath());
            imageView.setImageBitmap(localBitmap);

        } else if(requestCode == GlobalVars.RETURN_CODE_TAKE_PHOTO){
            Utils.debug("Create Seem Flow Activity - Pic Cancelled");
            ActivityFactory.finishActivity(this,Activity.RESULT_CANCELED);
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
                String mediaId = Api.createMedia(localBitmap);
                if(mediaId != null){
                    Seem seem = Api.createSeem(title,caption,mediaId);
                    AppSingleton.getInstance().saveSeem(seem);
                    return seem;
                }else {
                    Utils.debug("Error uploading");
                }
            }catch (Exception e) {
                Utils.debug("Pete al crear la imagen",e);
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
