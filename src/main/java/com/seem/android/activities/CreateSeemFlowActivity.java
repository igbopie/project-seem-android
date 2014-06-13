package com.seem.android.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.seem.android.service.Api;
import com.seem.android.R;
import com.seem.android.model.Seem;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by igbopie on 21/03/14.
 */
public class CreateSeemFlowActivity extends Activity  {


    String title;
    EditText titleText;
    Button submit;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seem_flow);

        submit = (Button) findViewById(R.id.seemItButton);
        titleText = (EditText) findViewById(R.id.seemTitleEditText);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = titleText.getText().toString();
                new CreateSeem().execute();
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
                //return Api.createSeem(title,calendar.getTime());
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





}
