package com.learn.notecatcam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.learn.notecatcam.adapters.ImageAdapter;
import com.learn.notecatcam.constant.StorageName;
import com.learn.notecatcam.constant.StorageVariable;
import com.learn.notecatcam.popup.Date_Time_Picker;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeviceActivity extends AppCompatActivity {
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    private final int GET_IMG_CODE= 101;
    private Scalar textColor = new Scalar(0, 0, 0); // black color
    private Scalar rectColor = new Scalar(255, 255, 255, 128); // semi-transparent white color (50% opacity)
    private int rectThickness = -1;
    private LocationManager locationManager;
    private Location currLocation;

    ArrayList<Bitmap> bitmaps;
    private GridView gridView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocationUpdates();

        /* Select Custom Date*/
        Button changeDateBtn = findViewById(R.id.change_date_btn);
        changeDateBtn.setOnClickListener(l->{
             new Date_Time_Picker().getDatePicker(this);
        });

        /* Select custom time */
        Button changeTimeBtn = findViewById(R.id.change_time_btn);
        changeTimeBtn.setOnClickListener(l->{
            new Date_Time_Picker().getTimePicker(this);
        });

        Button selectImgBtn = findViewById(R.id.select_img_btn);

        selectImgBtn.setOnClickListener(listener ->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intent.setType("image/*");
            startActivityForResult(intent,GET_IMG_CODE);
        });

            bitmaps = new ArrayList<>();
            gridView = findViewById(R.id.grid_view);

            imageAdapter = new ImageAdapter(this, bitmaps);
            gridView.setAdapter(imageAdapter);

//        gridView.setOnItemClickListener(new AdapterView().OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Bitmap selectedBitmap = bitmaps.get(position);
//            }
//        });
//    }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_IMG_CODE && resultCode ==RESULT_OK){
//            ImageView imageView = findViewById(R.id.image_view);
            bitmaps.clear();
            ClipData clipData = data.getClipData();

            if(clipData !=null){
                for(int i=0;i<clipData.getItemCount();i++){
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    try {
                        InputStream is= getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Utils.matToBitmap(putTextInImage(bitmap),bitmap);
                        bitmaps.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

            }else{

                Uri imageUri = data.getData();
                try {
                    InputStream is= getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Utils.matToBitmap(putTextInImage(bitmap),bitmap);
                    bitmaps.add(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            Log.e("here",String.valueOf(bitmaps.size()));
//            bitmaps.forEach(bitmap -> {
//                imageView.setImageBitmap(bitmap);
//
//            });

            imageAdapter.notifyDataSetChanged();
        }
    }

    public Mat putTextInImage(Bitmap bitmap){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);
        // Add note rectangle
        int rectWidth = 300;
        int rectHeight = 200;
        int rectX = 20;
        int rectY = mat.rows() - 20 - rectHeight; // Bottom-left corner
        Mat overlay = mat.clone();

        Imgproc.rectangle(overlay, new Point(rectX, rectY), new Point(rectX + rectWidth, rectY + rectHeight), rectColor, rectThickness);

        // Blend the overlay with the original frame
        Core.addWeighted(overlay, 0.5, mat, 0.5, 0, mat);


        if(currLocation!=null){
            Imgproc.putText(mat, "Latitude:  "+String.format("%.7f",currLocation.getLatitude()) , new Point(rectX + 10, rectY + rectHeight - 140), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Longitude: "+String.format("%.7f",currLocation.getLongitude()), new Point(rectX + 10, rectY + rectHeight - 110), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Altitude:  "+String.format("%.7f",currLocation.getAltitude()) , new Point(rectX + 10, rectY + rectHeight - 80), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Accuracy:  "+currLocation.getAccuracy() , new Point(rectX + 10, rectY + rectHeight - 50), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);

        }else{
            SharedPreferences preferences = getSharedPreferences(StorageName.NOTE_CAT_CAM.name(), Context.MODE_PRIVATE);

            Imgproc.putText(mat, "Latitude:  "+preferences.getString(StorageVariable.LATITUDE.name(),"25.2445333" ) , new Point(rectX + 10, rectY + rectHeight - 140), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Longitude: "+preferences.getString(StorageVariable.LONGITUDE.name(),"84.664717" ), new Point(rectX + 10, rectY + rectHeight - 110), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Altitude:  "+preferences.getString(StorageVariable.ALTITUDE.name(),"30.1") , new Point(rectX + 10, rectY + rectHeight - 80), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Accuracy:  "+preferences.getString(StorageVariable.ACCURACY.name(),"4.1") , new Point(rectX + 10, rectY + rectHeight - 50), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);


//            Imgproc.rectangle(overlay, new Point(rectX, rectY+150), new Point(rectX + rectWidth, rectY + rectHeight), rectColor, rectThickness);
//
//            // Blend the overlay with the original frame
//            Core.addWeighted(overlay, 0.5, mat, 0.5, 0, mat);
            Log.e("location:","Not Found location");
        }
        String noteText = "Time: "+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        Imgproc.putText(mat, noteText, new Point(rectX + 10, rectY + rectHeight - 20), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);

        return mat;
    }

    private void  startLocationUpdates() {

        try {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, location -> {
                        currLocation= location;
                    }
                    ,null);


        } catch (SecurityException e) {
            Log.e("Location Update", "Permission denied", e);
        }

    }
}