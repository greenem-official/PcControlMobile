package com.example.pcControl.tools;

import android.content.SharedPreferences;

import java.util.Set;

public class LoadData {
    public static String loadString (SharedPreferences sp, String name) {
        return sp.getString(name, "");
    }

    public static int loadInt (SharedPreferences sp, String name) {
        return sp.getInt(name,-5);
    }

    public static boolean loadBoolean (SharedPreferences sp, String name) {
        return sp.getBoolean(name,false);
    }

    public static float loadFloat (SharedPreferences sp, String name) {
        return sp.getFloat(name,-5);
    }

    public static long loadLong (SharedPreferences sp, String name) {
        return sp.getLong(name,-5);
    }

    public static Set<String> loadStringSet (SharedPreferences sp, String name) {
        return sp.getStringSet(name, null);
    }
}
