package com.example.pcControl.tools;

import android.content.SharedPreferences;

import java.util.Set;

public class SaveData {
    public static void save(SharedPreferences sp, String name, Boolean var) {
//        SharedPreferences sp = MainActivity.getInstance().getSharedPreferences(prefs, mode);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name, var);
        editor.apply();
    }

    public static void save(SharedPreferences sp, String name, String var) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, var);
        editor.apply();
    }

    public static void save(SharedPreferences sp, String name, float var) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(name, var);
        editor.apply();
    }

    public static void save(SharedPreferences sp, String name, int var) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(name, var);
        editor.apply();
    }

    public static void save(SharedPreferences sp, String name, Long var) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(name, var);
        editor.apply();
    }

    public static void save(SharedPreferences sp, String name, Set<String> var) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(name, var);
        editor.apply();
    }
}
