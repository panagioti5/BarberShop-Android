package com.barber.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.barber.app.adaptor.ShopsInAreaAdapter;
import com.barber.app.constants.Constants;
import com.barber.app.entities.Shop;
import com.barber.app.services.FavoritesShopsService;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FavoritesActivity extends AppCompatActivity {
    private final String activityTitle = "Favorite Shops";

    public List<Shop> shopList;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;
    public ShopsInAreaAdapter adapter;
    public AVLoadingIndicatorView avi;
    public SwipeRefreshLayout pullToRefresh;
    private ExtendedFloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(activityTitle);
        initializeScreenComponents();
        handleFabOperation();
        setOnRefreshListener();
        performNessecaryOperation(false);
        setListenerOnRecycleView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent homepage = new Intent(FavoritesActivity.this, MainActivity.class);
                homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homepage);
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent homepage = new Intent(FavoritesActivity.this, MainActivity.class);
            homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homepage);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeScreenComponents() {
        fab = findViewById(R.id.fab);
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

    private void handleFabOperation() {
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
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

    private void performNessecaryOperation(boolean isTriggeredFromRefresh) {
        avi.show();
        FavoritesShopsService favoritesShopsService = new FavoritesShopsService();
        favoritesShopsService.setContext(this);
        favoritesShopsService.setIsRefresh(isTriggeredFromRefresh);
        favoritesShopsService.findFavorites();
    }

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
            }
        });
    }
}