package edu.wwu.h20grow;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "plantDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PLANT = "plant";
    private static final String ID = "id";
    private static final String PLANT_NAME = "plantName";
    private static final String SCI_NAME = "sciName";
    private static final String CATEGORY = "category";
    private static final String WATERING_INTERVAL = "waterInterval";
    private static final String LAST_WATER_TIME = "lastWaterTime";
    private static final String IMGPATH = "imgPath";

    public DatabaseManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sqlCreate = "create table " + TABLE_PLANT + "( " + ID;
        sqlCreate += " integer primary key autoincrement, " + PLANT_NAME;
        sqlCreate += " text, " + SCI_NAME + " text, " + CATEGORY + " text, ";
        sqlCreate += WATERING_INTERVAL + " integer, " + LAST_WATER_TIME + " long, ";
        sqlCreate += IMGPATH + " text )";
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + TABLE_PLANT);
        onCreate(db);
    }

    public void insert(Plant plant){
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlInsert = "insert into " + TABLE_PLANT;
        sqlInsert += " values(null, '" + plant.getPlantName();
        sqlInsert += "', '" + plant.getSciName();
        sqlInsert += "', '" + plant.getCategory();
        sqlInsert += "', '" + plant.getWaterInterval();
        sqlInsert += "', '" + plant.getLastWaterTime();
        sqlInsert += "', '" + plant.getImgPath();
        sqlInsert += "' )";
        db.execSQL(sqlInsert);
        db.close();
    }

    public void deleteById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlDelete = "delete from " + TABLE_PLANT;
        sqlDelete += " where " + ID + " = " + id;
        db.execSQL(sqlDelete);
        db.close();
    }

    public ArrayList<Plant> selectAll(){
        String sqlQuery = "select * from " + TABLE_PLANT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        ArrayList<Plant> plants = new ArrayList<Plant>();

        while(cursor.moveToNext()){
            String temp = cursor.getString(5);
            Plant currentPlant = new Plant(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    Integer.parseInt(cursor.getString(4)),
                    Long.parseLong(cursor.getString(5)),
                    cursor.getString(6)

            );
            plants.add(currentPlant);
        }
        db.close();
        Collections.sort(plants);
        return plants;
    }

    public Plant selectByID(int id){
        String sqlQuery = "select * from " + TABLE_PLANT;
        sqlQuery += " where " + ID + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if(cursor.moveToNext()){
            Plant currentPlant = new Plant(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    Integer.parseInt(cursor.getString(4)),
                    Long.parseLong(cursor.getString(5)),
                    cursor.getString(6)

            );
            return currentPlant;
        }
        return null;
    }

    public void updateById( int id, String name, String sciName, String type, int waterInt, long lastWater, String imgPath ) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlUpdate = "update " + TABLE_PLANT;
        sqlUpdate += " set " + PLANT_NAME + " = '" + name + "', ";
        sqlUpdate += SCI_NAME + " = '" + sciName + "', ";
        sqlUpdate += CATEGORY + " = '" + type + "', ";
        sqlUpdate += WATERING_INTERVAL + " = '" + waterInt + "', ";
        sqlUpdate += LAST_WATER_TIME + " = '" + lastWater + "', ";
        sqlUpdate += IMGPATH + " = '" + imgPath + "' ";
        sqlUpdate += " where " + ID + " = " + id;

        db.execSQL( sqlUpdate );
        db.close( );
    }
    public void updateWater( int id, long lastWater ) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlUpdate = "update " + TABLE_PLANT;
        sqlUpdate += " set "+ LAST_WATER_TIME + " = '" + lastWater +"' ";
        sqlUpdate += " where " + ID + " = " + id;
        db.execSQL( sqlUpdate );
        db.close( );
    }
}

