package edu.wwu.h20grow;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Icon;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by carpend3 on 10/25/18.
 */

public class PlantDetailActivity extends AppCompatActivity{
    Bundle bundle;
    DatabaseManager dbm;
    int plantID;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_layout);
        dbm = new DatabaseManager(this);
        bundle = getIntent().getExtras();
        plantID = bundle.getInt("plantID");
        Plant myPlant = dbm.selectByID(plantID);
        TextView plantNameTV = (TextView) findViewById(R.id.detail_plant_name);
        TextView wateringIntervalTV = (TextView) findViewById(R.id.detail_watering_interval);
        TextView needs2bWateredTV = (TextView) findViewById(R.id.detail_needs_2b_watered);
        TextView needs2bWateredHeaderTV = (TextView) findViewById(R.id.detail_needs_2b_watered_header);
        ImageView plantImage = (ImageView) findViewById(R.id.detail_img);

        String plantName = myPlant.getPlantName();
        Date currDate = Calendar.getInstance().getTime();
        int waterInterval =myPlant.getWaterInterval();
        long pDate = myPlant.getLastWaterTime();
        long pInterval = java.util.concurrent.TimeUnit.HOURS.toMillis(waterInterval);
        long needsWater = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(pInterval - (currDate.getTime() - pDate));
        plantNameTV.setText(plantName);
        String interval;
        String next;
        if((waterInterval <= 24) && (waterInterval > 0))
            interval = "Every " + waterInterval + " Hours";
        else{
            interval = "Every " + waterInterval/24 + " Days";
        }

        if(needsWater < 0){
            needs2bWateredHeaderTV.setText("Needed To Be Watered:");
            if(needsWater <= -24){
                next = (needsWater/-24) + 1  + " Days Ago";
            }else{
                next = needsWater/-1 + " Hours Ago";
            }
        }else if(needsWater <= 24){
            next = "In " + needsWater + " Hours";
        }else {
            long temp = (needsWater/24) +1;
            next = "In " + temp + " Days";
        }
        wateringIntervalTV.setText(interval);
        needs2bWateredTV.setText(next);
        File imgFile = new File(myPlant.getImgPath());
        if(imgFile.exists())
        {

            Uri tempImage = Uri.fromFile(imgFile);
            Bitmap thisImage = BitmapFactory.decodeFile(getRealPath(tempImage));
            thisImage = checkRotate(thisImage, getRealPath(tempImage));
            plantImage.setImageBitmap(thisImage);

        }


    }

    public void editPlant(View v){
        Intent myIntent = new Intent(PlantDetailActivity.this, ActivityAdd.class);
        bundle = getIntent().getExtras();
        int plantID = bundle.getInt("plantID");
        myIntent.putExtra("plantID", plantID);
        PlantDetailActivity.this.startActivity(myIntent);
        this.finish();
    }
    public void water(View v){

        Plant myPlant = dbm.selectByID(plantID);
        long water = Calendar.getInstance().getTimeInMillis();
        myPlant.setLastWaterTime(water);
        TextView needs2bWateredTV = (TextView) findViewById(R.id.detail_needs_2b_watered);
        TextView needs2bWateredHeader = (TextView) findViewById(R.id.detail_needs_2b_watered_header);
        int waterInterval = myPlant.getWaterInterval();
        needs2bWateredHeader.setText("Needs To Be Watered:");
        if(waterInterval > 24){
            needs2bWateredTV.setText(" In " + waterInterval/24 + " Days ");
        }else {
            needs2bWateredTV.setText("In " + waterInterval + " Hours");
        }
        dbm.updateWater(plantID,water);
    }
    public void goBack(View v){
        this.finish();
    }

    public Bitmap checkRotate(Bitmap image, String path){
        Bitmap b = null;
        try{
            ExifInterface ex = new ExifInterface(path);
            int orient = ex.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orient){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    b = rotateImage(image, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    b = rotateImage(image, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    b = rotateImage(image, 270);
                    break;
                default:
                    b = image;
            }
        }catch (IOException e){
            Toast.makeText(this, "Error opening image", Toast.LENGTH_SHORT).show();
        }
        return b;
    }

    public Bitmap rotateImage(Bitmap image, int orient){
        Matrix matrix = new Matrix();
        matrix.postRotate(orient);

        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }

    public String getRealPath(Uri imgURI){
        String result;
        Cursor cursor = getContentResolver().query(imgURI, null, null, null, null);
        if(cursor == null){
            result = imgURI.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

}
