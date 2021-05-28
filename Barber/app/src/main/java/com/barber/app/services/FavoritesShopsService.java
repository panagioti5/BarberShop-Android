package com.barber.app.services;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.barber.app.FavoritesActivity;
import com.barber.app.cache.CacheManager;
import com.barber.app.constants.Constants;
import com.barber.app.entities.Shop;
import com.barber.app.utils.LocationManager;

import java.util.List;

public class FavoritesShopsService {
    private FavoritesActivity context;
    private boolean isTriggeredFromRefresh;

    public void setContext(FavoritesActivity mainActivity) {
        this.context = mainActivity;
    }

    public void findFavorites() {
        getShopsFromSharedPrefs();
    }

    private void getShopsFromSharedPrefs() {
        new LocationManager(context, false, false);
        context.pullToRefresh.setRefreshing(true);
        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    List<Shop> localShops = (List<Shop>) new CacheManager(context, "FAVS").read();
                    if (null == localShops || localShops.isEmpty()) {
                        throw new Exception(Constants.NOTHING_TO_SHOW);
                    }
                    context.shopList.clear();
                    context.shopList.addAll(localShops);
                } catch (final Exception e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.pullToRefresh.setRefreshing(false);
                        }
                    });
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                context.pullToRefresh.setRefreshing(false);
                context.adapter.notifyDataSetChanged();
                context.avi.hide();
                if (s != null) {
                    if (s.equals(Constants.NOTHING_TO_SHOW)) {
                        context.shopList.clear();
                    }
                }
            }
        };
        asyncTask.execute();
    }

    public void setIsRefresh(boolean isTriggeredFromRefresh) {
        this.isTriggeredFromRefresh = isTriggeredFromRefresh;
    }
}
