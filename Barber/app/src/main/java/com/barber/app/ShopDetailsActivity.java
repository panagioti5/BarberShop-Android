package com.barber.app;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.barber.app.adaptor.OverlapDecorationForWorkerListSlider;
import com.barber.app.adaptor.ShopDetailsSliderShowAdapter;
import com.barber.app.adaptor.WorkerSliderDataAdapter;
import com.barber.app.constants.Constants;
import com.barber.app.dao.SpecificShopEmployeeImageSelector;
import com.barber.app.entities.Address;
import com.barber.app.entities.OpeningHours;
import com.barber.app.entities.Shop;
import com.barber.app.entities.ShopImages;
import com.barber.app.entities.ShopWorkers;
import com.barber.app.entities.SliderItem;
import com.barber.app.services.GetSelectedShopsByIDService;
import com.barber.app.utils.AlertUtils;
import com.barber.app.utils.LocationManager;
import com.barber.app.utils.UtilsImpl;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView noInternetImg;
    private AVLoadingIndicatorView avi;
    private Shop selectedShop;
    private ImageView fab;
    private ShopDetailsSliderShowAdapter shopDetailsSliderShowAdapter;
    private WorkerSliderDataAdapter workersAdapter;
    private ScrollView scrollView;
    private ArrayAdapter servicesAdapter;
    private ListView servicesListView = null;
    private String[] services;
    private RecyclerView myRecyclerView;
    private GoogleMap mMap;
    private double shop_lon;
    private double shop_lat;
    private TextView workerNameSurname;
    private TextView workerDesc;
    private TextView shopAddress;
    private TextView telNum;
    private TextView telNumAlt;
    private TextView shopStatus;
    private SwipeRefreshLayout pullToRefresh;
    private TableLayout timeTable;
    private TableLayout tableDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocationManager(this, false, false);
        setContentView(R.layout.activity_shop_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hideAllScreenViews();
        initializeScreenComponents();
        initializeAdapterForEmployees();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            shop_lat = extras.getDouble(Constants.EXTRA_SESSION_ID_SHOP_LAT);
            shop_lon = extras.getDouble(Constants.EXTRA_SESSION_ID_SHOP_LON);
            setTitle(extras.getString(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE));
        }
        performNessecaryOperation(false);
        initializeRefreshListener();
        initializeServicesAdapter();
        initializeImagesSlider();
        showGMap();

    }

    private void initializeScreenComponents() {
        fab = (ImageView) findViewById(R.id.fab);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        workerDesc = (TextView) findViewById(R.id.workerDesc);
        workerNameSurname = (TextView) findViewById(R.id.workerMameSurname);
        shopAddress = (TextView) findViewById(R.id.shopAddress);
        telNum = (TextView) findViewById(R.id.shopTelNum);
        telNumAlt = (TextView) findViewById(R.id.shopTelNumAlt);
        shopStatus = (TextView) findViewById(R.id.shopStatus);
        myRecyclerView = findViewById(R.id.myRecyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        myRecyclerView.addItemDecoration(new OverlapDecorationForWorkerListSlider());
        myRecyclerView.setHasFixedSize(true);
        servicesListView = (ListView) findViewById(R.id.services_list);
        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);
        servicesListView.addFooterView(footerView);
        String indicator = getIntent().getStringExtra(Constants.INDICATOR);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.setIndicator(indicator);
        noInternetImg = findViewById(R.id.noInternetImg);
        timeTable = (TableLayout) findViewById(R.id.timeTable);
        tableDistance = (TableLayout) findViewById(R.id.tableDistance);
    }

    private void initializeServicesAdapter() {
        ListView listViewOfServices = (ListView) findViewById(R.id.services_list);
        servicesAdapter = new ArrayAdapter<String>(this,
                R.layout.services_listview);
        listViewOfServices.setAdapter(servicesAdapter);
    }

    private void initializeRefreshListener() {
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                performNessecaryOperation(true);
            }
        });
    }

    private void initializeAdapterForEmployees() {
        //For setting strings on worker image click we defined OnWorkerImageSelected which is implemented in adapter
        workersAdapter = new WorkerSliderDataAdapter(ShopDetailsActivity.this, new SpecificShopEmployeeImageSelector() {
            @Override
            public void onImageSelectedLine1(String change) {
                workerNameSurname.setText(change);
            }

            @Override
            public void onImageSelectedLine2(String change) {
                workerDesc.setText(change);
            }
        });
        myRecyclerView.setAdapter(workersAdapter);
    }

    private void showGMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (GMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    mMap.getUiSettings().setMapToolbarEnabled(true);
                    LatLng shopLoc = new LatLng(shop_lat, shop_lon);
                    mMap.addMarker(new
                            MarkerOptions().position(shopLoc).title(getSupportActionBar().getTitle().toString()
                    ));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(shopLoc, 15.0f));

                    scrollView = findViewById(R.id.scrollView);
                    ((GMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                            .setListener(new GMapFragment.OnTouchListener() {
                                @Override
                                public void onTouch() {
                                    scrollView.requestDisallowInterceptTouchEvent(true);
                                }
                            });
                }
            });
        }
    }

    private void initializeImagesSlider() {
        shopDetailsSliderShowAdapter = new ShopDetailsSliderShowAdapter(ShopDetailsActivity.this);
        SliderView sliderView = findViewById(R.id.imageSlider);
        sliderView.setSliderAdapter(shopDetailsSliderShowAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.RED);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }

    private void hideAllScreenViews() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.srl);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setVisibility(View.INVISIBLE);
        }
    }

    private void showAllScreenViews() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.srl);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setVisibility(View.VISIBLE);
        }
        noInternetImg.setVisibility(View.INVISIBLE);
        avi.hide();
        avi.setVisibility(View.INVISIBLE);
    }

    private void performNessecaryOperation(boolean isTriggeredFromRefresh) {
        performActionsWhenInternet(isTriggeredFromRefresh);
    }

    private void performActionsWhenInternet(boolean isTriggeredFromRefresh) {
        int shopID = 0;
        avi.show();
        avi.setVisibility(View.VISIBLE);
        getSingleShopFromRest(getApplicationContext(), shopID, isTriggeredFromRefresh);
    }

    private void getSingleShopFromRest(final Context context, final long shopID, boolean isTriggeredFromRefresh) {
        new LocationManager(this, false, false);
        @SuppressLint("StaticFieldLeak") final AsyncTask<Long, String, String> asyncTask = new AsyncTask<Long, String, String>() {
            @Override
            protected String doInBackground(Long... longs) {
                try {
                    GetSelectedShopsByIDService newsService = new GetSelectedShopsByIDService(context, shopID);
                    newsService.execute();
                    selectedShop = (Shop) newsService.getShopOutput();

                    if (selectedShop == null) {
                        throw new Exception(Constants.NOTHING_TO_SHOW);
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            setImageToFab(selectedShop);
                            setImagesInSlider(selectedShop);
                            setServicesInListView(selectedShop);
                            setWorkersInSlider(selectedShop);
                            setShopAddressAndContactDetails(selectedShop);
                            setShopStatus(selectedShop);
                            setShopWorkingHours(selectedShop);
                            setDistanceFromShop();
                        }
                    });

                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            avi.hide();
                            avi.setVisibility(View.INVISIBLE);
                            //CHECK FIRST LOAD OR REFRESH
                            if (selectedShop == null) {
                                //IN CASE WE VISITED THIS SCREEN WITH NO NET CONNECTION
                                hideAllScreenViews();
                                noInternetImg.setVisibility(View.VISIBLE);
                            } else if (selectedShop != null && e.getMessage().equals(Constants.SOMETHING_WENT_WRONG_NET)) {
                                noInternetImg.setVisibility(View.INVISIBLE);
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
                workersAdapter.notifyDataSetChanged();
                shopDetailsSliderShowAdapter.notifyDataSetChanged();
                if (s != null) {
                    if (s.equals(Constants.NOTHING_TO_SHOW) && selectedShop != null) {
                    } else {
                        if (isTriggeredFromRefresh) {
                            AlertUtils.showSimpleProblemSnackBar(ShopDetailsActivity.this, avi, s);
                        }
                    }
                } else {
                    showAllScreenViews();
                }
            }
        };
        asyncTask.execute(shopID);
    }

    private void setDistanceFromShop() {
        //THESE ARE COMING FROM PREVIOUS ACTIVITY BUNDLE AND NOT FROM API RESPONSE.
        String distanceFromShop = UtilsImpl.calculateDistanceFromShop(this, shop_lat, shop_lon);
        if (UtilsImpl.isNotNullOrEmpty(distanceFromShop)) {
            tableDistance.setVisibility(View.VISIBLE);
            distanceFromShop = !distanceFromShop.equals(Constants.NEARBY) ? distanceFromShop + " Away" : distanceFromShop;

            tableDistance.removeAllViews();
            TableRow tr = new TableRow(this);
            TextView column1Txt = new TextView(this);
            column1Txt.setText(distanceFromShop);
            column1Txt.setTextSize(18);
            column1Txt.setTextColor(Color.parseColor("#ADADAD"));
            column1Txt.setTypeface(null, Typeface.BOLD);
            column1Txt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
            column1Txt.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            column1Txt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showLocationAlert();
                }
            });

            ImageView column2Txt = new ImageView(this);
            column2Txt.setImageDrawable(getResources().getDrawable(R.drawable.info_icon));
            column2Txt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
            column2Txt.getLayoutParams().width = 70;
            column2Txt.getLayoutParams().height = 70;
            column2Txt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showLocationAlert();
                }
            });
            tr.addView(column1Txt);
            tr.addView(column2Txt);
            tableDistance.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.MATCH_PARENT));
        } else {
            tableDistance.setVisibility(View.INVISIBLE);
        }
    }

    private void showLocationAlert() {

        new MaterialAlertDialogBuilder(ShopDetailsActivity.this, R.style.AlertDialogTheme)
                .setTitle("Location Services")
                .setMessage("We use Location based services to calculate you distance from Shop. " +
                        "This depends on your mobile device signal. Use always \"GET DIRECTIONS\" button below to start navigation using external app.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }

    private void setShopWorkingHours(Shop selectedShop) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String todaysDayOfWeek = sdf.format(d);
        timeTable.removeAllViews();
        OpeningHours[] shopWorkingHours = selectedShop.getShopDetails().getOpeningHours();
        AtomicInteger dayOfWeek = new AtomicInteger();
        Arrays.stream(shopWorkingHours).forEach(x -> {
            TableRow tr = new TableRow(this);
            TextView days = new TextView(this);
            days.setText(x.getDayOfWeek().toString());
            days.setTextColor(Color.parseColor("#ADADAD"));
            days.setTextSize(14);
            if (todaysDayOfWeek.toLowerCase().equals(x.getDayOfWeek().toString().toLowerCase())) {
                days.setTypeface(null, Typeface.BOLD);
                days.setTextColor(Color.parseColor("#FFFFFF"));
                days.setTextSize(16);
            }

            days.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            days.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            TextView hours = new TextView(this);
            hours.setText(prepareShopWorkingHours(x));

            hours.setTextColor(Color.parseColor("#ADADAD"));
            hours.setTextSize(14);
            if (todaysDayOfWeek.toLowerCase().equals(x.getDayOfWeek().toString().toLowerCase())) {
                hours.setTypeface(null, Typeface.BOLD);
                hours.setTextColor(Color.parseColor("#FFFFFF"));
                hours.setTextSize(16);
            }

            hours.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            hours.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            tr.addView(days);
            tr.addView(hours);

            timeTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
            dayOfWeek.getAndIncrement();
        });
    }

    private String prepareShopWorkingHours(OpeningHours openingHours) {
        StringBuilder sb = new StringBuilder();
        if (openingHours.isOpen() == true) {
            String morningHours = "" + openingHours.getOpenFromUntil()[0];
            if (openingHours.getOpenFromUntil()[0] < 999) {
                morningHours = "0" + openingHours.getOpenFromUntil()[0];
            }
            sb.append(morningHours + " - " + openingHours.getOpenFromUntil()[1]);
            if (null != openingHours.getOpenFromUntil()[2] && null != openingHours.getOpenFromUntil()[3]) {
                sb.append("  |  " + openingHours.getOpenFromUntil()[2] + " - " + openingHours.getOpenFromUntil()[3]);
            }
        } else {
            sb.append("Closed");
        }
        return sb.toString();
    }

    private void setImagesInSlider(Shop selectedShop) {
        ShopImages[] fields = selectedShop.getShopDetails().getGalleryLinks();
        shopDetailsSliderShowAdapter.deleteAllItems();
        Arrays.stream(fields).forEach(s -> {
            SliderItem sliderItem = new SliderItem();
            sliderItem.setDescription(s.getImageDescription());
            sliderItem.setImageUrl(s.getImageLink());
            shopDetailsSliderShowAdapter.addItem(sliderItem);
        });
    }

    private void setWorkersInSlider(Shop selectedShop) {
        workerNameSurname.setText(selectedShop.getShopDetails().getShopWorkers().get(0).getName() + " " + selectedShop.getShopDetails().getShopWorkers().get(0).getSurname());
        workerDesc.setText(selectedShop.getShopDetails().getShopWorkers().get(0).getWorkerDesc());
        List<ShopWorkers> workers = selectedShop.getShopDetails().getShopWorkers();
        workersAdapter.deleteAllItems();
        workers.stream().forEach(s -> {
            ShopWorkers sliderItem = new ShopWorkers();
            sliderItem.setWorkerProfilePictureLink(s.getWorkerProfilePictureLink());
            sliderItem.setName(s.getName());
            sliderItem.setSurname(s.getSurname());
            sliderItem.setWorkerDesc(s.getWorkerDesc());
            workersAdapter.addItem(sliderItem);
        });
    }

    private void setImageToFab(Shop selectedShop) {
        Glide.with(this)
                .asBitmap()
                .load(selectedShop.getLogoLink())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        fab.setImageBitmap(UtilsImpl.getRoundedCroppedBitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void setShopStatus(Shop selectedShop) {
        String selectedShopStatus = selectedShop.getStatusMessage();
        shopStatus.setText(selectedShopStatus);
        if (selectedShopStatus.equals(Constants.SHOP_CLOSED)) {
            shopStatus.setTextColor(Color.parseColor(Constants.SHOP_CLOSED_COLOR));
            shopStatus.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        if (selectedShopStatus.equals(Constants.SHOP_CLOSES_SOON)) {
            shopStatus.setTextColor(Color.parseColor(Constants.SHOP_CLOSES_SOON_COLOR));
            shopStatus.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        if (selectedShopStatus.equals(Constants.SHOP_OPEN)) {
            shopStatus.setTextColor(Color.parseColor(Constants.SHOP_OPEN_COLOR));
            shopStatus.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    private void setShopAddressAndContactDetails(Shop selectedShop) {
        Address selectedShopAddress = selectedShop.getShopDetails().getShopAddress();
        shopAddress.setText(selectedShop.getShopDetails().getCity() + " " + selectedShopAddress.getArea() + ", " + selectedShopAddress.getStreetName() + " " + selectedShopAddress.getBuildingNum() + Constants.LINE_BREAK + selectedShopAddress.getPostalCode());
        telNum.setText("" + selectedShop.getShopDetails().getPhoneNum());
        telNumAlt.setText("" + selectedShop.getShopDetails().getPhoneNumAlt());
    }


    private void setServicesInListView(Shop selectedShop) {
        services = Arrays.copyOfRange(selectedShop.getShopDetails().getProviderServices().split(";"), 0, 3);
        services = Arrays.stream(services).map(x -> x = "•      " + x).toArray(String[]::new);
        servicesAdapter.clear();
        servicesAdapter.addAll((Object[]) services);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void seeAllServices(View v) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(selectedShop.getShopDetails().getProviderServices().split(";")).forEach(x -> sb.append("• ").append(x).append(Constants.LINE_BREAK).append(Constants.LINE_BREAK));
        new MaterialAlertDialogBuilder(ShopDetailsActivity.this, R.style.AlertDialogTheme)
                .setTitle("Shop Services")
                .setMessage(sb)
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                scrollView.getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                scrollView.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        // Handle MapView's touch events.
        super.dispatchTouchEvent(ev);
        return true;
    }

    public void getDirections(View v) {
        String latitude = String.valueOf(shop_lat);
        String longitude = String.valueOf(shop_lon);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            if (mapIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        } catch (NullPointerException e) {
        }
    }

    public void bookAppointment(View v) {
        Dialog customDialog = new CustomDialogBookAppointment(this, selectedShop.getShopDetails());
        customDialog.show();
        customDialog.setCanceledOnTouchOutside(false);
    }
}
