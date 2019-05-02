package edu.wwu.h20grow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity{

    android.support.v7.widget.SearchView sv;
    DatabaseManager dbm;
    Button closeButton;
    PopupWindow popupWindow;
    LinearLayout linearLayout1;
    ArrayList<Plant> plants;
    RecyclerView rv;
    public static Settings user_settings;
    Context context;
    MyAdapter adapter;
    View view;
    Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        user_settings = new Settings(this);
        dbm = new DatabaseManager(this);
        rv= (RecyclerView) findViewById(R.id.myRecycler);
        if(dbm.selectAll().size() > 0){
            plants = dbm.selectAll();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, ActivityAdd.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        ImageButton sort = (ImageButton) findViewById(R.id.sortButton);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);


        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.sort_layout, null);
                popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.showAtLocation(linearLayout1, Gravity.CENTER, 0, 0);

                closeButton = (Button) customView.findViewById(R.id.popup_close);
                Button WNAButton = customView.findViewById(R.id.WNA_button);
                Button WNDButton = customView.findViewById(R.id.WND_button);
                Button alphabeticalButton = customView.findViewById(R.id.alphabetical_button);
                Button alphabeticalButton2 = customView.findViewById(R.id.alphabetical_button2);


                WNAButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        Collections.sort(plants);
                        popupWindow.dismiss();
                        cardView(plants);

                    }
                });

                WNDButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        Collections.sort(plants);
                        Collections.reverse(plants);

                        popupWindow.dismiss();
                        cardView(plants);

                    }
                });
                alphabeticalButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        Collections.sort(plants, new Comparator<Plant>() {
                            @Override
                            public int compare(Plant plant1, Plant plant2) {
                                return (plant1.getPlantName().toLowerCase()
                                        .compareTo(plant2.getPlantName().toLowerCase()));
                            }
                        });
                        popupWindow.dismiss();
                        cardView(plants);

                    }
                });

                alphabeticalButton2.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        Collections.sort(plants, new Comparator<Plant>() {
                            @Override
                            public int compare(Plant plant1, Plant plant2) {
                                return (plant2.getPlantName().toLowerCase()
                                        .compareTo(plant1.getPlantName().toLowerCase()));
                            }
                        });
                        popupWindow.dismiss();
                        cardView(plants);

                    }
                });



                closeButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        popupWindow.dismiss();
                    }
                });

            }
        });

        ImageButton settings = findViewById(R.id.settingButton);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.settings_layout, null);

                closeButton = (Button) customView.findViewById(R.id.popup_close);

                final CheckBox pNotify = customView.findViewById(R.id.push_notify);
                final CheckBox deleteConfirm = customView.findViewById(R.id.delete_confirm);
                final CheckBox calendarEv = customView.findViewById(R.id.calendar_event);

                pNotify.setChecked(user_settings.ispNotify());
                deleteConfirm.setChecked(user_settings.isDeleteConfrim());
                calendarEv.setChecked(user_settings.isCalendarEv());

                pNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(pNotify.isChecked()){
                            user_settings.setpNotify(true);
                        }else{
                            user_settings.setpNotify(false);
                        }

                        user_settings.setPreferences(context);
                    }
                });

                deleteConfirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(deleteConfirm.isChecked()){
                            user_settings.setDeleteConfirm(true);
                        }else{
                            user_settings.setDeleteConfirm(false);
                        }

                        user_settings.setPreferences(context);
                    }
                });

                calendarEv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(calendarEv.isChecked()){
                            user_settings.setCalendarEv(true);
                        }else{
                            user_settings.setCalendarEv(false);
                        }

                        user_settings.setPreferences(context);
                    }
                });

                popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.showAtLocation(linearLayout1, Gravity.CENTER, 0, 0);

                closeButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        popupWindow.dismiss();
                    }
                });

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        dbm = new DatabaseManager(this);
        plants = dbm.selectAll();
        cardView(plants);
        initSwipe();
    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
                int position = viewHolder.getAdapterPosition();

                if(direction == ItemTouchHelper.LEFT){
                    if(user_settings.isDeleteConfrim()){
                        deleteConfirm(position);
                    }else{
                        Plant plant = plants.get(position);
                        dbm.deleteById(plant.getId());
                        adapter.removeItem(position);
                        Toast.makeText(context,plant.getPlantName() + " Has Been Deleted",Toast.LENGTH_SHORT).show();
                    }
                    onResume();
                }else{
                    Plant plant = plants.get(position);
                    long water = Calendar.getInstance().getTimeInMillis();
                    plant.setLastWaterTime(water);
                    dbm.updateWater(plant.getId(),water);
                    Toast.makeText(context,plant.getPlantName() + " Has Been Watered",Toast.LENGTH_SHORT).show();
                    onResume();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive){
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height/3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#1d7fad"));
                        RectF background = new RectF((float) itemView.getLeft(),
                                (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_opacity_white_18dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width,
                                (float) itemView.getTop() + width,
                                (float) itemView.getLeft() + 2*width,
                                (float) itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest, p);
                    }else{
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX,
                                (float) itemView.getTop(),
                                (float) itemView.getRight(),
                                (float) itemView.getBottom());
                        c.drawRect(background, p);
                        //TEMP ICON
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width,
                                (float) itemView.getTop() + width,
                                (float) itemView.getRight() - width,
                                (float) itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    private void deleteConfirm(final int position){
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.confirm_layout, null);

        Button cancelButton = customView.findViewById(R.id.popup_cancel);
        Button confirmButton = customView.findViewById(R.id.popup_confirm);

        popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(linearLayout1, Gravity.CENTER, 0, 0);

        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                popupWindow.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Plant plant = plants.get(position);
                dbm.deleteById(plant.getId());
                adapter.removeItem(position);
                Toast.makeText(context,plant.getPlantName() + " Has Been Deleted",Toast.LENGTH_SHORT).show();
                onResume();
                popupWindow.dismiss();
            }
        });
    }

    private void cardView(ArrayList<Plant> plants){
        sv = findViewById(R.id.mSearch);

        rv = findViewById(R.id.myRecycler);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());


        if (plants.size() >= 1) {
            TextView textView = findViewById(R.id.emptyScreen);
            textView.setText("");
        }else{
            TextView textView = findViewById(R.id.emptyScreen);
            textView.setText("You don't have any plants added! \n Use the \'+\' button in the bottom right to add new plants");

        }

        adapter = new MyAdapter(this, plants);
        rv.setAdapter(adapter);

        sv.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener()

        {
            @Override
            public boolean onQueryTextSubmit (String query){
                return false;
            }

            @Override
            public boolean onQueryTextChange (String query){
                //filters plants while you type
                adapter.getFilter().filter(query);
                return false;
            }
        });

        stopService(new Intent(this, TimeTracker.class));
        startService(new Intent(this, TimeTracker.class));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}
