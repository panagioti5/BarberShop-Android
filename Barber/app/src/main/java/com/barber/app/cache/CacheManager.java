package com.barber.app.cache;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


import com.barber.app.entities.Shop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CacheManager {

    private final Context context;
    private final String storeFileName;
    private SharedPreferences sharedPreferences;


    public CacheManager(Activity context, String storeFileName) {
        this.context = context;
        this.storeFileName = storeFileName;
    }

    public void save(List<?> list) {
        sharedPreferences = context.getSharedPreferences(storeFileName, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(list);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Set", json);
        editor.commit();
    }

    public List<?> read() {
        sharedPreferences = context.getSharedPreferences(storeFileName, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Set", "");
        if (json.isEmpty()) {
            return null;
        } else {
            Type type = new TypeToken<List<Shop>>() {
            }.getType();
            List<?> arrPackageData = gson.fromJson(json, type);
            return arrPackageData;
        }
    }
}
