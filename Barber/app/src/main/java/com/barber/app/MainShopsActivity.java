package com.barber.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.barber.app.adaptor.ShopsInAreaAdapter;
import com.barber.app.adaptor.search.ShopsInAreaSearchAdapter;
import com.barber.app.constants.Constants;
import com.barber.app.entities.Shop;
import com.barber.app.services.GetAPIGenericService;
import com.barber.app.services.GetAllShopsForAreaService;
import com.barber.app.services.Service;
import com.barber.app.utils.AlertUtils;
import com.barber.app.utils.LocationManager;
import com.barber.app.utils.UtilsImpl;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class MainShopsActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton fab;
    private List<Shop> shopList;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;
    private ShopsInAreaAdapter adapter;
    private AVLoadingIndicatorView avi;
    private String activityTitle = Constants.EMPTY_STRING;
    private Boolean loading = false;
    private ImageView noInternetImg;
    private SwipeRefreshLayout pullToRefresh;
    private FloatingActionMenu fabmenu;
    private View searchLayout;
    private ShopsInAreaSearchAdapter shopsInAreaSearchAdapter;
    private List<Shop> searchUtilList = new ArrayList<>();
    private RecyclerView searchRecycleView;
    private GridLayoutManager searchGridLayout;
    private boolean isCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocationManager(this, false, false);
        setContentView(R.layout.activity_cut_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            activityTitle = extras.getString(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE);
            setTitle(activityTitle);
        }

        initializeScreenComponents();
        setFabListener();
        handleStatusBarAppearance();
        setOnRefreshListener();
        performNessecaryOperation(false);

        searchUtilList = UtilsImpl.getList(Constants.SEARCH_SHOPS, getApplicationContext(), Constants.SEARCH_SHOPS);


        //TODO replace below if to check whether if we are in the correct city. Currently city is not populated in Android !!!!
        //  if (searchUtilList.isEmpty() || !searchUtilList.get(0).getShopDetails().getCity().equals(activityTitle))
        //  if its old data fetch again
        if (searchUtilList.isEmpty())
        {
            isCalled = true;
            getShopsForSearchUtilFromRest();
        }
        if (!isCalled && !searchUtilList.get(0).getShopDetails().getCity().equals(activityTitle))
        {
            isCalled = true;
            getShopsForSearchUtilFromRest();
        }
         if (isCallExpired() && !isCalled)
         {
             isCalled = true;
             getShopsForSearchUtilFromRest();
         }
    }


    private boolean isCallExpired()
    {
        long currDate = new Date().getTime();

        SharedPreferences prefs = getSharedPreferences(Constants.SEARCH_SHOPS_DATE_UPDATED, MODE_PRIVATE);
        long lastTimeUpdated = prefs.getLong(Constants.SEARCH_SHOPS_DATE_UPDATED, -1);
        if (lastTimeUpdated == -1)
        {
            return true;
        }
        if(currDate > lastTimeUpdated + Constants.MINUTES_FOR_SEARCH_API_EXPIRED)
        {
            return true;
        }
        return false;
    }

    private void setOnRefreshListener() {
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                performNessecaryOperation(true);
                pullToRefresh.setRefreshing(true);
            }
        });
    }

    private void handleStatusBarAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void setFabListener() {
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void initializeScreenComponents() {
//        searchView = findViewById(R.id.searchBar);
//        searchView.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));
        fabmenu = findViewById(R.id.fabmenu);
        fab = findViewById(R.id.fab);
        noInternetImg = findViewById(R.id.noInternetImg);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        String indicator = getIntent().getStringExtra(Constants.INDICATOR);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.setIndicator(indicator);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this.getApplicationContext()));
        shopList = new ArrayList<>();
        gridLayout = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayout);
        adapter = new ShopsInAreaAdapter(this, shopList);
        recyclerView.setAdapter(adapter);
    }

    private void performNessecaryOperation(boolean isTriggeredFromRefresh) {
        performActionsWhenInternet(isTriggeredFromRefresh);
    }

    private void performActionsWhenNoInternet() {
        avi.hide();
        showRetryMessage();
    }

    private void performActionsWhenInternet(boolean isTriggeredFromRefresh) {
        avi.show();
        getShopsFromRest(getApplicationContext(), 0, isTriggeredFromRefresh);
        setListenerOnRecycleView();
    }

    private void showRetryMessage() {
        AlertUtils.showSimpleProblemSnackBar(MainShopsActivity.this, avi, Constants.NETWORK_UNAVAILABLE);
    }

//    public void onClick(View v) {
//        InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
//        im.showSoftInput(searchView, 0);
//    }

    private void setListenerOnRecycleView() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (dy > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fab.hide();
                            fab.setVisibility(View.GONE);
                        }
                    });
                }

                if (dy < 0 && gridLayout.findLastCompletelyVisibleItemPosition() >= 6) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fab.show();
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
                }
                if (gridLayout.findLastCompletelyVisibleItemPosition() <= 6) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fab.hide();
                            fab.setVisibility(View.GONE);
                        }
                    });
                }
                //calling api multiple times with different id. Affected by speed of phone and network.
                if (!loading) {
                    if (dy > 0 && gridLayout.findLastCompletelyVisibleItemPosition() == shopList.size() - 4
                            && linearLayoutManager != null
                            && linearLayoutManager.findLastCompletelyVisibleItemPosition() == shopList.size() - 4
                            && shopList.get(shopList.size() - 1).getRankID() % 10 == 0) {
                        loading = true;
                        getShopsFromRest(getApplicationContext(), shopList.get(shopList.size() - 1).getRankID(), true);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.cutnewsmenu,menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cutnewsmenu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                shopsInAreaSearchAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            searchLayout = findViewById(R.id.search_layout);
            searchLayout.setVisibility(View.VISIBLE);

            searchUtilList = UtilsImpl.getList(Constants.SEARCH_SHOPS, getApplicationContext(), Constants.SEARCH_SHOPS);
            //TODO replace below if to check whether if we are in the correct city
            //  if (searchUtilList.isEmpty() || !searchUtilList.get(0).getShopDetails().getCity().equals(activityTitle))
            //  Need to delete the below get shops from below danger on listener !!!! getShopsForSearchUtilFromRest();
            //  if (searchUtilList.isEmpty())
            //  {
            //  getShopsForSearchUtilFromRestgetShopsForSearchUtilFromRest();
            //  }
            searchRecycleView = (RecyclerView) findViewById(R.id.search_recyclerView);
            searchRecycleView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
            searchGridLayout = new GridLayoutManager(this, 1);
            searchRecycleView.setLayoutManager(searchGridLayout);
            shopsInAreaSearchAdapter = new ShopsInAreaSearchAdapter(this, searchUtilList);
            searchRecycleView.setAdapter(shopsInAreaSearchAdapter);
            return true;
        }
        if (id == android.R.id.home) {
            if (searchLayout != null && searchLayout.getVisibility() == View.VISIBLE) {
                searchLayout.setVisibility(View.INVISIBLE);

            } else {
                Intent homepage = new Intent(MainShopsActivity.this, MainActivity.class);
                startActivity(homepage);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getShopsFromRest(final Context context, final int id, boolean isTriggeredFromRefresh) {
        String TAG = "MainShopsActivity";
        Log.e(TAG, "CALLING WITH:@: " + id);
        new LocationManager(this, false, false);
        pullToRefresh.setRefreshing(true);
        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    Service newsService = new GetAllShopsForAreaService(context, activityTitle);
                    newsService.setRankIndex(id);
                    newsService.execute();
                    List<Shop> localShops = (List<Shop>) newsService.getRESTOutput();

                    if (localShops.isEmpty()) {
                        throw new Exception(Constants.NOTHING_TO_SHOW);
                    }
                    if (id == 0) {
                        shopList.clear();
                    }
                    shopList.addAll(localShops);
                    noInternetImg.setVisibility(View.INVISIBLE);
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pullToRefresh.setRefreshing(false);
                            if (shopList.isEmpty() && e.getMessage().equals(Constants.SOMETHING_WENT_WRONG_NET)) {
                                noInternetImg.setVisibility(View.VISIBLE);
                            } else {
                                noInternetImg.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                pullToRefresh.setRefreshing(false);
                loading = false;
                adapter.notifyDataSetChanged();
                avi.hide();
                fabmenu.setVisibility(View.VISIBLE);
                if (s != null) {
                    if (s.equals(Constants.NOTHING_TO_SHOW) && !shopList.isEmpty()) {
                    } else {
                        if (isTriggeredFromRefresh) {
                            AlertUtils.showSimpleProblemSnackBar(MainShopsActivity.this, avi, s);
                        }
                    }
                }
            }
        };
        asyncTask.execute(id);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (searchLayout != null && searchLayout.getVisibility() == View.VISIBLE) {
                    searchLayout.setVisibility(View.INVISIBLE);

                } else {
                    Intent homepage = new Intent(MainShopsActivity.this, MainActivity.class);
                    homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homepage);
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void getShopsForSearchUtilFromRest() {
        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    GetAPIGenericService allShopFromSearchAPI = new GetAPIGenericService();
                    allShopFromSearchAPI.setURL(prepareURL(activityTitle));
                    allShopFromSearchAPI.execute();
                    Log.e("DATE", "CAAAAAAAAAAAAAAAAAALLING");
                    List<Shop> localShops = (List<Shop>) allShopFromSearchAPI.getRESTOutput();
                    if (localShops.isEmpty()) {
                        throw new Exception(Constants.NOTHING_TO_SHOW);
                    }
                    saveShopsForSearchUtil(localShops);
                    noInternetImg.setVisibility(View.INVISIBLE);
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pullToRefresh.setRefreshing(false);
                            if (searchUtilList.isEmpty() && e.getMessage().equals(Constants.SOMETHING_WENT_WRONG_NET)) {
                                noInternetImg.setVisibility(View.VISIBLE);
                            } else {
                                noInternetImg.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                pullToRefresh.setRefreshing(false);
                loading = false;
                if(null != shopsInAreaSearchAdapter) {
                    shopsInAreaSearchAdapter.notifyDataSetChanged();
                }
                avi.hide();
                fabmenu.setVisibility(View.VISIBLE);
                if (s != null) {
                    if (s.equals(Constants.NOTHING_TO_SHOW) && !shopList.isEmpty()) {
                    } else {

                    }
                }
            }
        };
        asyncTask.execute();
    }

    private void saveShopsForSearchUtil(List<Shop> localShops)
    {
        UtilsImpl.setSharePreferencesFromList(Constants.SEARCH_SHOPS, localShops, Constants.SEARCH_SHOPS, getApplicationContext());
        searchUtilList.addAll((UtilsImpl.getList(Constants.SEARCH_SHOPS, getApplicationContext(), Constants.SEARCH_SHOPS)));

        SharedPreferences.Editor editor = getSharedPreferences(Constants.SEARCH_SHOPS_DATE_UPDATED, MODE_PRIVATE).edit();
        editor.putLong(Constants.SEARCH_SHOPS_DATE_UPDATED, new Date().getTime());
        editor.apply();
    }

    private URL prepareURL(String shopCity) throws Exception {
        Properties properties = new Properties();
        AssetManager assetManager = getApplicationContext().getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
        return new URL(properties.getProperty("url") + properties.getProperty("searchAllShopsByCityURI") + Constants.SINGLE_SLASH + shopCity);
    }

}

