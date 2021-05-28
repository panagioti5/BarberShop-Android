package com.barber.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.barber.app.constants.Constants;
import com.barber.app.entities.Shop;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class UtilsImpl {
    public static String calculateDistanceFromShop(Activity mContext, double shopLatitude, double shopLongitude) {

        if (GPSUtils.checkPermissions(mContext)) {
            if (GPSUtils.isLocationEnabled(mContext)) {

                SharedPreferences prefs = mContext.getSharedPreferences(Constants.SHOP_PACK, MODE_PRIVATE);
                //TODO TEST THIS:
                /*
                 *
                 * Open the app on your place. Goto limassol shops with location and internet turned on.. Then screenshot the shops ( shop with rank 7 is in Limassol ). Now
                 * turn off location and internet.
                 *Drive to other location.
                 *Open app -> goto limassol and it distance must be same. Now turn on location and internet ands try to refresh it. Will the location distance change?
                 * when will it change? Check this things...
                 *
                 *
                 *
                 * 2nd test = refresh page while moving
                 * */
                if (prefs != null) {

                    String lon = prefs.getString(Constants.GPS_LON, "");
                    String lat = prefs.getString(Constants.GPS_LAT, "");

                    if (UtilsImpl.isNotNullOrEmpty(lon) && UtilsImpl.isNotNullOrEmpty(lat)) {

                        Location locationA = new Location("point A");
                        locationA.setLongitude(shopLongitude);
                        locationA.setLatitude(shopLatitude);

                        double devLongitude;
                        double devLatitute;

                        try {
                            devLongitude = Double.parseDouble(lon);
                            devLatitute = Double.parseDouble(lat);
                        } catch (NumberFormatException ex) {
                            return "";
                        }

                        Location locationB = new Location("point A");

                        locationB.setLongitude(devLongitude);
                        locationB.setLatitude(devLatitute);
                        DecimalFormat df = new DecimalFormat("#.#");
                        float distance = locationA.distanceTo(locationB) / 1000;

                        if (distance < 0.05) {

                            return Constants.NEARBY;
                        }
                        return String.valueOf(df.format(distance)) + " Km";
                    }
                }
            }
        }
        return "";
    }

    public static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isNotNullOrEmptyPassword(String value) {
        return value != null && !value.isEmpty();
    }


    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static boolean isUserSignedIn(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(Constants.SHOP_PACK, MODE_PRIVATE);
        return prefs != null && null != prefs.getString(Constants.CONNECTED_ACCOUNT, "") && !prefs.getString(Constants.CONNECTED_ACCOUNT, "").isEmpty()
                && null != prefs.getString(Constants.IS_CONNECTED, "") && !prefs.getString(Constants.IS_CONNECTED, "").isEmpty();
    }

    public static boolean isValueWithoutSpecialCharacters(String value) {
        Pattern p = Pattern.compile("^[A-Za-z]+$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(value);
        return m.find();
    }

    public static <T> void setSharePreferencesFromList(String key, List<T> list, String sharePreferenceName, Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        set(key, json, context, sharePreferenceName);
    }

    public static void set(String key, String value, Context context, String sharePreferenceName) {
        SharedPreferences.Editor editor = context.getSharedPreferences(sharePreferenceName, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static List<Shop> getList(String sharePreferenceName, Context context, String key) {
        List<Shop> arrayItems = new ArrayList<>();
        String serializedObject = context.getSharedPreferences(sharePreferenceName, MODE_PRIVATE).getString(key, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            arrayItems = gson.fromJson(serializedObject, new com.google.gson.reflect.TypeToken<List<Shop>>() {
            }.getType());
        }
        return arrayItems;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}