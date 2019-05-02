package edu.wwu.h20grow;

import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by carpend3 on 11/2/18.
 */

public class Plant implements Comparable<Plant>{
    private int id;
    private String plantName;
    private String sciName;
    private String category;
    private int waterInterval;
    private long lastWaterTime;
    private String imgPath;

    public Plant(int id, String plantName, String sciName, String category, int waterInterval, long lastWaterTime, String imgPath) {
        this.id = id;
        this.sciName = sciName;
        this.plantName = plantName;
        this.category = category;
        this.waterInterval = waterInterval;
        if(lastWaterTime > 0){
            this.lastWaterTime = lastWaterTime;
        }else{
            this.lastWaterTime = Calendar.getInstance().getTimeInMillis();
        }

        this.imgPath = imgPath;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSciName() {
        return sciName;
    }

    public void setSciName(String sciName) {
        this.sciName = sciName;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getWaterInterval() {
        return waterInterval;
    }

    public void setWaterInterval(int waterInterval) {
        this.waterInterval = waterInterval;
    }

    public long getLastWaterTime() {
        return lastWaterTime;
    }

    public void setLastWaterTime(long currWaterTime) {
        this.lastWaterTime = currWaterTime;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int compareTo(Plant comparePlant){
        Date currDate = Calendar.getInstance().getTime();
        long pDate = this.getLastWaterTime();
        long pInterval = TimeUnit.HOURS.toMillis(this.getWaterInterval());
        long timeUntilWater = pInterval - (currDate.getTime() - pDate);
        pDate = comparePlant.getLastWaterTime();
        pInterval = TimeUnit.HOURS.toMillis(comparePlant.getWaterInterval());
        long compareWater = pInterval - (currDate.getTime() - pDate);
        int bigMath = (int) TimeUnit.MILLISECONDS.toMinutes(timeUntilWater - compareWater);
        return bigMath;
    }
}
