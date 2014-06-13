package com.seem.android.customviews;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.seem.android.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by igbopie on 13/06/14.
 */
public class TimeAndDatePicker  extends RelativeLayout implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, View.OnClickListener {


    Calendar calendar = Calendar.getInstance();

    TextView timeTextView;
    TextView dateTextView;

    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

    public TimeAndDatePicker(final Context context, AttributeSet attrs) {
        super(context,attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{}, 0, 0);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_time_and_date_picker, this, true);

        timeTextView = (TextView) findViewById(R.id.timeTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);

        this.set1Day();

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = new TimePickerDialog(context,TimeAndDatePicker.this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                tpd.show();
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(context,TimeAndDatePicker.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        findViewById(R.id.button5Min).setOnClickListener(this);
        findViewById(R.id.button30Min).setOnClickListener(this);
        findViewById(R.id.button1Hour).setOnClickListener(this);
        findViewById(R.id.button6Hour).setOnClickListener(this);
        findViewById(R.id.button1Day).setOnClickListener(this);
        findViewById(R.id.button1Week).setOnClickListener(this);
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


    public void set5Min(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        setDate(calendar);
    }

    public void set30Min(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,30);
        setDate(calendar);
    }

    public void set1Hour(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,1);
        setDate(calendar);
    }

    public void set6Hour(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,6);
        setDate(calendar);
    }

    public void set1Day(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        setDate(calendar);
    }

    public void set1Week(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR,1);
        setDate(calendar);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button5Min:
                set5Min();
                break;
            case R.id.button30Min:
                set30Min();
                break;
            case R.id.button1Hour:
                set1Hour();
                break;
            case R.id.button6Hour:
                set6Hour();
                break;
            case R.id.button1Day:
                set1Day();
                break;
            case R.id.button1Week:
                set1Week();
                break;
        }
    }
}
