package com.example.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.popularmovies.R;

public class MoviesPreferences {

    public static boolean sorOrderPopular(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortOrder = context.getString(R.string.pref_sort_order_key);
        String defaultSortOrder = context.getString(R.string.pref_sort_order_popular);
        String preferredSortOrder = prefs.getString(keyForSortOrder, defaultSortOrder);
        String popular = context.getString(R.string.pref_sort_order_popular);
        boolean userPrefersPopular = popular.equals(preferredSortOrder);
        return userPrefersPopular;
    }
}