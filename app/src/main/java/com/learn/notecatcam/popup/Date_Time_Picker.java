package com.learn.notecatcam.popup;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.learn.notecatcam.R;

import java.nio.Buffer;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Date_Time_Picker {

    public void getDatePicker(Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.date_picker_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        Button set_date_btn = dialog.findViewById(R.id.set_date_btn);
        set_date_btn.setOnClickListener(l->{
           int day= datePicker.getDayOfMonth();
           int month= datePicker.getMonth()+1;
           int year = datePicker.getYear();
           Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,day);
           Date date= calendar.getTime();
           Log.e("Date:-",date.toString());
            dialog.dismiss();
        });
        dialog.show();


    }

    public void getTimePicker(Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.time_picker_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        TimePicker time= dialog.findViewById(R.id.time_picker);
        int hour=time.getHour();
        int min=time.getMinute();
        Log.e("time:-", hour +":"+ min);

        dialog.show();
    }

}
