package com.seem.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.seem.android.model.Topic;
import com.seem.android.service.Api;
import com.seem.android.GlobalVars;
import com.seem.android.R;
import com.seem.android.model.Seem;
import com.seem.android.service.SeemService;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
    Boolean cameraStarted = false;
    private Spinner topicSpinner;
    List<String> topicNameList;
    ArrayAdapter<String> dataAdapter;

    GlobalVars.PhotoSource source;
    private List<Topic> topics = new ArrayList<Topic>();
    private Topic topicSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seem_flow);

        imageView = (ImageView) findViewById(R.id.previewImageView);
        editText = (EditText) findViewById(R.id.captionEditText);
        submit = (Button) findViewById(R.id.seemItButton);
        titleText = (EditText) findViewById(R.id.seemTitleEditText);
        topicSpinner = (Spinner) findViewById(R.id.topicSpinner);

        topicNameList = new ArrayList<String>();
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, topicNameList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicSpinner.setAdapter(dataAdapter);
        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position == 0){
                    topicSelected = null;
                } else {
                   topicSelected = topics.get(position-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                topicSelected = null;
            }
        });


        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                Utils.debug(getClass(),"onEditorAction: "+actionId);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    topicSpinner.performClick();
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
            //Do nothing
        } else if(requestCode == GlobalVars.RETURN_CODE_GALLERY && resultCode == Activity.RESULT_OK){
            localTempFile =data.getData();
        } else {
            Utils.debug(this.getClass(),"Create Seem Flow Activity - Pic Cancelled");
            ActivityFactory.finishActivity(this,Activity.RESULT_CANCELED);
        }

        if(resultCode == Activity.RESULT_OK){
            Utils.loadStream(data.getData(),imageView,this);
            new FetchTopics().execute();
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
                    String topicId = null;
                    if(topicSelected != null){
                        topicId = topicSelected.getId();
                    }
                    return SeemService.getInstance().save(title, caption,topicId, mediaId);
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


    public class FetchTopics extends AsyncTask<Void,Void,Void>{
        private final ProgressDialog dialog = new ProgressDialog(CreateSeemFlowActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            topics = Api.getTopics();

            Utils.debug(getClass(),"Topics: "+topics);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

            topicNameList.clear();
            topicNameList.add("None");
            for(Topic topic:topics){
                topicNameList.add(topic.getName());
            }
            dataAdapter.notifyDataSetChanged();

            titleText.performClick();

            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(titleText, 0);
        }
    }

}
