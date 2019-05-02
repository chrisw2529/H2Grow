package edu.wwu.h20grow;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by carpend3 on 12/4/18.
 */

public class Settings {
    private final String PREFERENCE_PUSH_NOTIFY = "pNotify";
    private final String PREFERENCE_DELETE_CONFIRM = "deleteConfrim";
    private final String PREFERENCE_CALENDAR_EV = "calendarEv";

    private boolean pNotify;
    private boolean deleteConfirm;
    private boolean calendarEv;

    public Settings(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        setpNotify(pref.getBoolean(PREFERENCE_PUSH_NOTIFY, true));
        setDeleteConfirm(pref.getBoolean(PREFERENCE_DELETE_CONFIRM, true));
        setCalendarEv(pref.getBoolean(PREFERENCE_CALENDAR_EV, false));
    }

    public void setPreferences(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREFERENCE_PUSH_NOTIFY, pNotify);
        editor.putBoolean(PREFERENCE_DELETE_CONFIRM, deleteConfirm);
        editor.putBoolean(PREFERENCE_CALENDAR_EV, calendarEv);
        editor.commit();
    }

    public void setpNotify(boolean pNotify) {
        this.pNotify = pNotify;
    }

    public void setDeleteConfirm(boolean pNotifyVib) {
        this.deleteConfirm = pNotifyVib;
    }

    public void setCalendarEv(boolean calendarEv) {
        this.calendarEv = calendarEv;
    }


    public boolean ispNotify() {
        return pNotify;
    }

    public boolean isDeleteConfrim() {
        return deleteConfirm;
    }

    public boolean isCalendarEv() {
        return calendarEv;
    }
}


