package com.barber.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.barber.app.constants.Constants;

import androidx.appcompat.app.AppCompatActivity;

public class DataProtectionActivity extends AppCompatActivity {

    private final String activityTitle = "Data Protection";
    private TextView dataProtection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_protection);
        getSupportActionBar().setTitle(activityTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dataProtection = findViewById(R.id.dataProtection);
        setTextToDataProtection();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
//                Intent homepage = new Intent(DataProtectionActivity.this, MainActivity.class);
//                homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(homepage);
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            Intent homepage = new Intent(DataProtectionActivity.this, MainActivity.class);
//            homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(homepage);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTextToDataProtection() {
        StringBuilder sb = new StringBuilder();
        sb.append("\u25BA   " + Constants.DB_1);
        sb.append(Constants.LINE_BREAK);
        sb.append(Constants.LINE_BREAK);
        sb.append("\u25BA   " + Constants.DB_2);
        sb.append(Constants.LINE_BREAK);
        sb.append(Constants.LINE_BREAK);
        sb.append("\u25BA   " + Constants.DB_3);
        sb.append(Constants.LINE_BREAK);
        sb.append(Constants.LINE_BREAK);
        sb.append("\u25BA   " + Constants.DB_4);
        sb.append(Constants.LINE_BREAK);
        sb.append(Constants.LINE_BREAK);
        sb.append("\u25BA   " + Constants.DB_5);
        sb.append(Constants.LINE_BREAK);
        sb.append(Constants.LINE_BREAK);
        sb.append("\u25BA   " + Constants.DB_6);
        sb.append(Constants.LINE_BREAK);
        sb.append(Constants.LINE_BREAK);
        sb.append("\u25BA   " + Constants.DB_7);
        sb.append(Constants.LINE_BREAK);
        sb.append(Constants.LINE_BREAK);
        dataProtection.setText(sb.toString());
        dataProtection.setTextSize(20);
    }
}