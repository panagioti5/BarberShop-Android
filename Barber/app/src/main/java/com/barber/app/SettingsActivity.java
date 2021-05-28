package com.barber.app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.barber.app.DAOImpl.firetoken.FireTokenDaoImpl;
import com.barber.app.constants.Constants;
import com.barber.app.dao.FireTokenDaoIntf;
import com.barber.app.utils.AlertUtils;
import com.barber.app.utils.UtilsImpl;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private final String activityTitle = "Settings";
    private CheckBox acceptsNotifications;
    private String originalState;
    private AVLoadingIndicatorView avi;
    private CircleImageView lan_gr;
    private CircleImageView lan_en;
    private String sysLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();
        setTitle(activityTitle);
        acceptsNotifications = findViewById(R.id.acceptsNotifications);
        setSettingsBasedOnSP();
        displayLangCircleImages();
        setSelectedLanguage();
    }

    private void setSelectedLanguage() {
        SharedPreferences lanPref = getSharedPreferences(Constants.SYS_LAN, MODE_PRIVATE);
        String userSelectedLanguage = lanPref.getString(Constants.SELECTED_LANGUAGE, null);
        if (!UtilsImpl.isNotNullOrEmpty(userSelectedLanguage)) {
            lan_en.setBorderColor(Color.parseColor("#5EBA7D"));
            lan_en.setBorderWidth(14);
            lan_gr.setBorderWidth(0);
        } else {
            if ("EN".equals(userSelectedLanguage)) {
                setEnglishLan();
            } else {
                setGreekLan();
            }
        }
    }

    private void displayLangCircleImages() {
        lan_gr = (CircleImageView) findViewById(R.id.lan_gr);
        lan_en = (CircleImageView) findViewById(R.id.lan_en);

        Picasso.get()
                .load(R.drawable.lan_en)
                .resize(150, 150)
                .centerCrop()
                .into(lan_en);

        Picasso.get()
                .load(R.drawable.lan_gr)
                .resize(150, 150)
                .centerCrop()
                .into(lan_gr);
    }

    private void setSettingsBasedOnSP() {
        SharedPreferences accountPrefs = getSharedPreferences("NOTIFICATIONS", MODE_PRIVATE);
        String userAcceptsNotifications = accountPrefs.getString(Constants.ACCEPTS_PUSH_NOTIFICATIONS, "");
        if (userAcceptsNotifications.equals("AN")) {
            originalState = "AN";
            acceptsNotifications.setChecked(true);
        } else {
            originalState = "NAN";
        }
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


    public void proceedSave(View v) {
        saveAcceptPushNotificationsResult();
        saveLanguageSP();
        saveToRemoteStore();
    }


    private void rollBackChanges() {
        if (originalState.equals("AN")) {
            acceptsNotifications.setChecked(true);
        } else {
            acceptsNotifications.setChecked(false);
        }
    }

    private void saveToRemoteStore() {
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.srlSettings);
        enableDisableView(myLayout, false);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        String indicator = getIntent().getStringExtra(Constants.INDICATOR);
        avi.setIndicator(indicator);
        avi.setVisibility(View.VISIBLE);
        avi.show();
        callRegisterToken(UtilsImpl.isUserSignedIn(this));
    }

    private void saveAcceptPushNotificationsResult() {
        SharedPreferences.Editor editor = getSharedPreferences("NOTIFICATIONS", MODE_PRIVATE).edit();
        String result = acceptsNotifications.isChecked() ? "AN" : "NAN";
        editor.putString(Constants.ACCEPTS_PUSH_NOTIFICATIONS, result);
        editor.apply();
    }

    public void cancelSettings(View v) {
        finish();
    }

    private void callRegisterToken(boolean isUserSignedIn) {

        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    FireTokenDaoIntf tokenToDB = new FireTokenDaoImpl(SettingsActivity.this, isUserSignedIn, isUserSignedIn);
                    tokenToDB.register();
                } catch (Exception e) {
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.srlSettings);
                enableDisableView(myLayout, true);
                avi.hide();
                avi.setVisibility(View.INVISIBLE);

                if (s != null) {
                    AlertUtils.showSimpleProblemSnackBar(SettingsActivity.this, lan_gr, "Some settings are not saved. Make sure you have internet connection.");
                    rollBackChanges();
                } else {
                    finish();
                }
            }
        };
        asyncTask.execute();
    }

    private void enableDisableView(View view, boolean enabled) {
        view.setClickable(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    public void changeLanguageEffect(View view) {
        if (view.getId() == R.id.lan_en) {
            setEnglishLan();
        }
        if (view.getId() == R.id.lan_gr) {
            setGreekLan();
        }
    }

    private void setEnglishLan() {
        lan_en.setBorderColor(Color.parseColor("#5EBA7D"));
        lan_en.setBorderWidth(14);
        lan_gr.setBorderWidth(0);
        sysLanguage = "EN";
    }

    private void setGreekLan() {
        lan_gr.setBorderColor(Color.parseColor("#5EBA7D"));
        lan_gr.setBorderWidth(14);
        lan_en.setBorderWidth(0);
        sysLanguage = "GR";
    }


    private void saveLanguageSP() {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SYS_LAN, MODE_PRIVATE).edit();
        editor.putString(Constants.SELECTED_LANGUAGE, sysLanguage);
        editor.apply();

    }
}