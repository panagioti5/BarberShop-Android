package com.barber.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.barber.app.DAOImpl.firetoken.FireTokenDaoImpl;
import com.barber.app.adaptor.ShopsInAreaAdapter;
import com.barber.app.constants.Constants;
import com.barber.app.dao.FireTokenDaoIntf;
import com.barber.app.entities.Shop;
import com.barber.app.services.MainScreenPopularShopsService;
import com.barber.app.utils.LocationManager;
import com.barber.app.utils.UtilsImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private final String drawerTitle = "Sign In";
    private final String activityTitle = "Shops";
    public List<Shop> shopList;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;
    public ShopsInAreaAdapter adapter;
    public AVLoadingIndicatorView avi;
    public ImageView noInternetImg;
    public SwipeRefreshLayout pullToRefresh;
    public NavigationView navigationView;
    private ExtendedFloatingActionButton fab;
    private static final int MENU_ITEM_ITEM1 = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocationManager(this, true, true);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.drawer_lines);
        setTitle(activityTitle);
        initializeScreenComponents();
        handleFabOperation();
        handleStatusBarAppearance();
        handleSignedUserDrawerAppearance();
        handleNotificationFromFire();
        saveFireTokenToFireIfExisit();
        setOnRefreshListener();
        handleDrawerActionsListener();
        performNessecaryOperation(false);
        setListenerOnRecycleView();
        changeNavigationDrawerUIcon();
    }

    private void handleDrawerActionsListener() {
        MainScreenDrawerHandler mainScreenDrawerHandler = new MainScreenDrawerHandler(MainActivity.this);
        mainScreenDrawerHandler.handleDrawer();
    }

    private void changeNavigationDrawerUIcon() {
        MainScreenDrawerHandler mainScreenDrawerHandler = new MainScreenDrawerHandler(MainActivity.this);
        mainScreenDrawerHandler.handleUIDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer((int) Gravity.LEFT);
        }
    }

    private void makeScreenAnimate() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mainAnim);
        AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
        animation.setFillAfter(true);
        animation.setDuration(1000);
        animation.setStartOffset(50);
        layout.startAnimation(animation);
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

    private void handleStatusBarAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void initializeScreenComponents() {
        fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
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

    private void saveFireTokenToFireIfExisit() {
        SharedPreferences prefs = getSharedPreferences(Constants.FIRE_TOKEN, MODE_PRIVATE);
        String token = prefs.getString("token", "");
        System.out.println("TOKEEEN:"+ token);
        if (TextUtils.isEmpty(token)) {
            saveCurrentInstanceTokenOfFirebaseToAppServer();
        }
    }

    private void handleNotificationFromFire() {
        //INTENT FROM NOTIFICATION (FIREBASE)
        if (getIntent().getExtras() != null) {
            Object value = getIntent().getExtras().get("body");
            if (null != value) {
                String valueFromSpringNotification = value.toString();
                if (valueFromSpringNotification.equals(Constants.TITLE_NICOSIA) || valueFromSpringNotification.equals(Constants.TITLE_PAPHOS) ||
                        valueFromSpringNotification.equals(Constants.TITLE_LIMASSOL) ||
                        valueFromSpringNotification.equals(Constants.TITLE_LARNACA)) {
                    Intent newsPage = new Intent(MainActivity.this, MainShopsActivity.class);
                    newsPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, value.toString());
                    startActivity(newsPage);
                    finish();
                }
            }
        }
    }

    private void handleSignedUserDrawerAppearance() {
        if (UtilsImpl.isUserSignedIn(MainActivity.this)) {
            changeNavigationDrawerItemTitle(navigationView);
        } else {
            Menu menu = navigationView.getMenu();
            MenuItem signOut = menu.findItem(R.id.nav_logout);
            signOut.setVisible(false);
            MenuItem nav_login = menu.findItem(R.id.nav_login);
            nav_login.setTitle(drawerTitle);
        }
    }

    private void performNessecaryOperation(boolean isTriggeredFromRefresh) {
        avi.show();
        MainScreenPopularShopsService mainScreenPopularShopsService = new MainScreenPopularShopsService();
        mainScreenPopularShopsService.setContext(this);
        mainScreenPopularShopsService.setIsRefresh(isTriggeredFromRefresh);
        mainScreenPopularShopsService.findPopular();
    }

    private void changeNavigationDrawerItemTitle(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        MenuItem nav_login = menu.findItem(R.id.nav_login);
        SharedPreferences accountPrefs = MainActivity.this.getSharedPreferences(Constants.SHOP_PACK, MODE_PRIVATE);
        String connectedAccount = accountPrefs.getString(Constants.CONNECTED_ACCOUNT, "");
        int index = connectedAccount.indexOf("@");
        nav_login.setTitle(connectedAccount.substring(0, index));
        nav_login.setIcon(null);
        MenuItem signOut = menu.findItem(R.id.nav_logout);
        signOut.setVisible(true);
    }

    private void saveCurrentInstanceTokenOfFirebaseToAppServer() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        System.out.println("TOKEEEN:"+ token);
                        SharedPreferences.Editor editor = getSharedPreferences(Constants.FIRE_TOKEN, MODE_PRIVATE).edit();
                        if (!TextUtils.isEmpty(token)) {
                            editor.putString("token", token);
                            editor.apply();
                            editor.commit();
                            boolean isUserSignedIn = UtilsImpl.isUserSignedIn(MainActivity.this);
                            try {
                                FireTokenDaoIntf tokenToDB = new FireTokenDaoImpl(MainActivity.this, isUserSignedIn, isUserSignedIn);
                                tokenToDB.register();
                            } catch (Exception e) {
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.openDrawer((int) Gravity.LEFT);
            }
            return true;
        }
        if (item.getItemId() == R.id.settingsGeneral) {
            Intent newsPage = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(newsPage);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
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

