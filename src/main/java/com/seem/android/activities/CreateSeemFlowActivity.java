package com.seem.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.seem.android.GlobalVars;
import com.seem.android.customviews.TimeAndDatePicker;
import com.seem.android.service.Api;
import com.seem.android.R;
import com.seem.android.model.Seem;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.util.Date;

/**
 * Created by igbopie on 21/03/14.
 */
public class CreateSeemFlowActivity extends Activity  {
    ImageView imageView;

    boolean newCover = false;
    Uri uri;
    TimeAndDatePicker startDateTimeAndDatePicker;
    TimeAndDatePicker endDateTimeAndDatePicker;
    EditText titleText;
    Spinner publishPermissionsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seem_flow);

        startDateTimeAndDatePicker = (TimeAndDatePicker) findViewById(R.id.startDateTimeAndDatePicker);
        endDateTimeAndDatePicker = (TimeAndDatePicker) findViewById(R.id.endDateTimeAndDatePicker);


        startDateTimeAndDatePicker.unset();
        endDateTimeAndDatePicker.unset();

        Button submit = (Button) findViewById(R.id.seemItButton);
        titleText = (EditText) findViewById(R.id.seemTitleEditText);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateSeem().execute();
            }
        });

        publishPermissionsSpinner = (Spinner) findViewById(R.id.publishPermissionsSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.publish_permissions_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        publishPermissionsSpinner.setAdapter(adapter);

        imageView = (ImageView) findViewById(R.id.coverPhotoImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(CreateSeemFlowActivity.this, imageView);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.actionPopupCamera:
                                Utils.debug(this.getClass(), "NEW SEEM!");
                                uri = Utils.getNewFileUri();
                                ActivityFactory.startCamera(CreateSeemFlowActivity.this,uri);
                                return true;
                            case R.id.actionPopupGallery:
                                Utils.debug(this.getClass(), "NEW SEEM!");
                                ActivityFactory.startGallery(CreateSeemFlowActivity.this);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

    }


    private class CreateSeem extends AsyncTask<Void,Void,Seem> {
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

                String title = titleText.getText().toString();
                Date startDate = startDateTimeAndDatePicker.getDate();
                Date endDate = endDateTimeAndDatePicker.getDate();
                String permission = publishPermissionsSpinner.getSelectedItem().toString();
                String coverPhotoMediaId = null;
                Utils.debug(getClass(),"P:"+permission);

                //Upload cover photo
                if(newCover){
                    coverPhotoMediaId = Api.createMedia(getContentResolver().openInputStream(uri));
                }
                return Api.createSeem(title,startDate,endDate,coverPhotoMediaId,permission);


            }catch (Exception e) {
                Utils.debug(this.getClass(),"Pete al crear el seem",e);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.RETURN_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //DO nothing
            newCover = true;
        }  else if(requestCode == GlobalVars.RETURN_CODE_GALLERY && resultCode == Activity.RESULT_OK){
            uri =data.getData();
            newCover = true;
        } else {
            Utils.debug(this.getClass(),"Reply Flow Activity - Pic Cancelled");
            ActivityFactory.finishActivity(this,Activity.RESULT_CANCELED);
        }

        if(resultCode == Activity.RESULT_OK){
            Utils.loadStream(uri,imageView,this);
        }

    }




}
