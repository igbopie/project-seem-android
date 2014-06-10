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
public class CreateSeemFlowActivity extends Activity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {


    String title;
    EditText titleText;
    Button submit;
    TextView timeTextView;
    TextView dateTextView;

    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

    Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seem_flow);

        submit = (Button) findViewById(R.id.seemItButton);
        titleText = (EditText) findViewById(R.id.seemTitleEditText);

        timeTextView = (TextView) findViewById(R.id.timeTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);

        this.set1Day(null);

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment(CreateSeemFlowActivity.this);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment newFragment = new DatePickerFragment(CreateSeemFlowActivity.this);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

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
                return Api.createSeem(title,calendar.getTime());
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



    private void setDate(Calendar date){
        this.calendar = date;
        timeTextView.setText(""+timeFormat.format(date.getTime()));
        dateTextView.setText(""+dateFormat.format(date.getTime()));
    }

    private Calendar getDate(){
        return this.calendar;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar calendar = getDate();
        calendar.set(year, month, day);
        setDate(calendar);

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Calendar calendar = getDate();
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        setDate(calendar);
    }


    public void set5Min(View view){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        setDate(calendar);
    }

    public void set30Min(View view){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,30);
        setDate(calendar);
    }

    public void set1Hour(View view){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,1);
        setDate(calendar);
    }

    public void set6Hour(View view){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,6);
        setDate(calendar);
    }

    public void set1Day(View view){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        setDate(calendar);
    }

    public void set1Week(View view){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR,1);
        setDate(calendar);
    }


    public static class TimePickerFragment extends DialogFragment{
        TimePickerDialog.OnTimeSetListener onTimeSetListener;

        public TimePickerFragment(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
            this.onTimeSetListener = onTimeSetListener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }


    }

    public static class DatePickerFragment extends DialogFragment {

        DatePickerDialog.OnDateSetListener onDateSetListener;

        public DatePickerFragment(DatePickerDialog.OnDateSetListener onDateSetListener) {
            this.onDateSetListener = onDateSetListener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
        }
    }

}
