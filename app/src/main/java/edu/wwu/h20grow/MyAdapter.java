package edu.wwu.h20grow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static edu.wwu.h20grow.R.*;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> implements Filterable {

    Context c;
    ArrayList<Plant> plants,filterList;
    CustomFilter filter;
    DatabaseManager dbm;


    public MyAdapter(Context ctx, ArrayList<Plant> plants)
    {
        this.c=ctx;
        this.filterList=plants;
        dbm = new DatabaseManager(ctx);
        this.plants=plants;

    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(layout.cards_layout,null);
        MyHolder holder=new MyHolder(v);
        return holder;
    }

    private int changeTransparency(int color){
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        alpha *= 0.5;

        return Color.argb(alpha, red, green, blue);



    }
    //Decides what to display on the cards in the main activity
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        Date currDate = Calendar.getInstance().getTime();
        long pDate = plants.get(position).getLastWaterTime();
        long pInterval = java.util.concurrent.TimeUnit.HOURS.toMillis(plants.get(position).getWaterInterval());
        long needsWater = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(pInterval - (currDate.getTime() - pDate));
        holder.plantColorIcon.setColorFilter(Color.argb(255,37, 112, 32));
        if(needsWater <= 0){
            //TODO: start here change to icon
            //holder.cardLayout.setBackgroundColor(0xff0000);
            holder.plantColorIcon.setColorFilter(Color.argb(255,168, 23, 23));

            if(needsWater <= -24){
                holder.waterTxt.setText(String.format(Locale.US, "Needed Water: %d Days Ago", (needsWater/-24)+1));
            }else{
                holder.waterTxt.setText(String.format(Locale.US, "Needed Water: %d Hours Ago", needsWater/-1));
            }
        }else if(needsWater > 24) {

            holder.waterTxt.setText(String.format(Locale.US, "Needs Water In: %d Days", (needsWater/24)+1));
        }else {
            if(needsWater < 10) {
                holder.plantColorIcon.setColorFilter(Color.argb(255, 214, 191, 19));
            }
            holder.waterTxt.setText(String.format(Locale.US, "Needs Water In: %d Hours", needsWater));
        }
        holder.nameTxt.setText(plants.get(position).getPlantName());
        Plant myPlant = plants.get(position);

        File imgFile = new File(myPlant.getImgPath());
        if(imgFile.exists()){
            //resizes the bitmap to be displayed in the main activity if the image is really big
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap smallBM = Bitmap.createScaledBitmap(bitmap, 140, 140, true);
            smallBM = checkRotate(smallBM, imgFile.getAbsolutePath());
            holder.img.setImageBitmap(smallBM);

        }else{
           holder.img.setImageResource(drawable.temp_plant);
        }



        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent myIntent = new Intent(v.getContext(), PlantDetailActivity.class);
                int plantId = plants.get(pos).getId();
                myIntent.putExtra("plantID", plantId);
                v.getContext().startActivity(myIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(filterList,this);
        }

        return filter;
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

        }
        return b;
    }

    public void addItem(Plant plant){
        plants.add(plant);
        notifyItemInserted(plants.size());
    }

    public void removeItem(int position){
        plants.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, plants.size());
    }

    public Bitmap rotateImage(Bitmap image, int orient){
        Matrix matrix = new Matrix();
        matrix.postRotate(orient);

        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }
}
