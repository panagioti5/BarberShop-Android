package com.barber.app.services;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;

import com.barber.app.MainActivity;
import com.barber.app.cache.CacheManager;
import com.barber.app.constants.Constants;
import com.barber.app.dao.ShopDAO;
import com.barber.app.entities.Shop;
import com.barber.app.factory.Factory;
import com.barber.app.utils.AlertUtils;
import com.barber.app.utils.LocationManager;

import java.util.List;

public class MainScreenPopularShopsService {
    private MainActivity context;
    private boolean isTriggeredFromRefresh;

    public void setContext(MainActivity mainActivity) {
        this.context = mainActivity;
    }

    public void findPopular() {
        getShopsFromRest();
    }

    private void getShopsFromRest() {
        new LocationManager(context, false, false);
        context.pullToRefresh.setRefreshing(true);
        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    ShopDAO userInterface = new Factory().getPopularShops();
                    List<Shop> localShops = userInterface.getAllPopularShops(context);

                    if (localShops.isEmpty()) {
                        throw new Exception(Constants.NOTHING_TO_SHOW);
                    }
                    context.shopList.clear();
                    context.shopList.addAll(localShops);
                    context.noInternetImg.setVisibility(View.INVISIBLE);
                    cacheResultsForNextLoad(localShops);
                } catch (final Exception e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.pullToRefresh.setRefreshing(false);
                            if (context.shopList.isEmpty() && e.getMessage().equals(Constants.SOMETHING_WENT_WRONG_NET)) {
                                context.noInternetImg.setVisibility(View.VISIBLE);
                            } else {
                                context.noInternetImg.setVisibility(View.INVISIBLE);
                            }
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
                    loadShopsFromCache();
                    if (s.equals(Constants.NOTHING_TO_SHOW) && !context.shopList.isEmpty()) {
                    } else {
                        if (isTriggeredFromRefresh) {
                            AlertUtils.showNetworkProblemSnackBar(context, context.navigationView, s);
                        }
                    }
                }
            }
        };
        asyncTask.execute();
    }


    private void loadShopsFromCache() {
        CacheManager cacheManager = new CacheManager(context, "POPULAR_SHOPS");
        List<?> cachedShops = cacheManager.read();
        if (null != cachedShops && !cachedShops.isEmpty()) {
            context.shopList.clear();
            context.shopList.addAll((List<Shop>) cachedShops);
            context.noInternetImg.setVisibility(View.INVISIBLE);
            context.pullToRefresh.setRefreshing(false);
            context.adapter.notifyDataSetChanged();
            context.avi.hide();
        }
    }

    private void cacheResultsForNextLoad(List<Shop> localShops) {
        CacheManager cacheManager = new CacheManager(context, "POPULAR_SHOPS");
        cacheManager.save(localShops);
    }

    public void setIsRefresh(boolean isTriggeredFromRefresh) {
        this.isTriggeredFromRefresh = isTriggeredFromRefresh;
    }
}
