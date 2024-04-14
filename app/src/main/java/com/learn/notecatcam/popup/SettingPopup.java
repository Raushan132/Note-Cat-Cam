package com.learn.notecatcam.popup;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.learn.notecatcam.R;

public class SettingPopup {
    RadioGroup radioGroup;


    public  void showDialog(Context context){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.setting_popup_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Button saveBtn = dialog.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v-> dialog.dismiss());


        dialog.show();
        radioGroup = dialog.findViewById(R.id.date_time_format);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if(selectedId==-1){
                Toast.makeText(context,"Nothing selected", Toast.LENGTH_SHORT).show();
            }else{
             RadioButton radioButton =  dialog.findViewById(selectedId);
             String val= String.valueOf(radioButton.getText());
             Log.e("selected radio btn",val);
            }
        });
    }
}
