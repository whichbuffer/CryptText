package com.cryptext.app;

import android.app.Application;

import com.cryptext.utils.DBForNotes;


public class App extends Application {

    private static boolean isInForeground = true;
    private static int timesInBackground; //0 by default
    private static boolean isAnActivityCounting; //false by default
    private static DBForNotes db;
    private static String singleNote;

    public static int getTimesInBackground() {
        return timesInBackground;
    }

    public static boolean isInForeground() {
        return isInForeground;
    }

    public static boolean isAnActivityCounting() {
        return isAnActivityCounting;
    }

    public static void setIsAnActivityCounting(final boolean newState) {
        isAnActivityCounting = newState;
    }

    public static void setIsInForeground(final boolean newState) {
        isInForeground = newState;
        timesInBackground = 0;
    }

    public static void incTimesInBackground() {
        timesInBackground++;
    }

    public static void setDatabase(DBForNotes database) {
        db = database;
    }

    public static DBForNotes getDatabase() {
        return db;
    }

    public static void setNote(String note) {
        singleNote = note;
    }

    public static String getNote() {
        return singleNote;
    }
}