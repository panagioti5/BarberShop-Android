package com.barber.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.barber.app.constants.Constants;
import com.barber.app.dao.EventBus;
import com.barber.app.entities.UserDetails;
import com.barber.app.services.AccountDetailsBYMailService;
import com.barber.app.services.UpdateAccountDetailsService;
import com.barber.app.utils.AlertUtils;
import com.barber.app.utils.UtilsImpl;
import com.barber.app.validators.UpdateAccountDetailsValidator;
import com.google.common.eventbus.Subscribe;

import androidx.appcompat.app.AppCompatActivity;

public class AccountDetailsActivity extends AppCompatActivity {

    private final String activityTitle = "Account Details";
    private String userEmail;
    private String userPass;
    private UserDetails userDetails;
    private EditText nameRegister;
    private EditText passwordRegister;
    private EditText phoneRegister;
    private CheckBox emailNotifications;
    private CheckBox acceptsNotifications;
    public String oldToken;


    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(activityTitle);

        SharedPreferences prefs = this.getSharedPreferences(Constants.SHOP_PACK, MODE_PRIVATE);
        if (prefs != null && null != prefs.getString(Constants.CONNECTED_ACCOUNT, "") && !prefs.getString(Constants.CONNECTED_ACCOUNT, "").isEmpty()
                && null != prefs.getString(Constants.IS_CONNECTED, "") && !prefs.getString(Constants.IS_CONNECTED, "").isEmpty()
                && null != prefs.getString(Constants.CONNECTED_ACCOUNT_PWD, "")
                && !prefs.getString(Constants.CONNECTED_ACCOUNT_PWD, "").isEmpty()) {
            userEmail = prefs.getString(Constants.CONNECTED_ACCOUNT, "");
            userPass = prefs.getString(Constants.CONNECTED_ACCOUNT_PWD, "");
        }
        callRetrieveDetailsService();
    }

    private void callRetrieveDetailsService() {
        if (UtilsImpl.isNotNullOrEmpty(userEmail) && UtilsImpl.isNotNullOrEmpty(userPass)) {
            AccountDetailsBYMailService accountDetailsBYMailService = new AccountDetailsBYMailService();
            accountDetailsBYMailService.setContext(this);
            accountDetailsBYMailService.setUserEmail(userEmail);
            accountDetailsBYMailService.setUserPass(userPass);
            accountDetailsBYMailService.retrieve();
        } else {
            Intent homepage = new Intent(AccountDetailsActivity.this, MainActivity.class);
            homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homepage);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
//                Intent homepage = new Intent(AccountDetailsActivity.this, MainActivity.class);
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
//            Intent homepage = new Intent(AccountDetailsActivity.this, MainActivity.class);
//            homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(homepage);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void proceedUpdateDetails(View view) {
        if (UtilsImpl.isNotNullOrEmpty(userEmail) && UtilsImpl.isNotNullOrEmpty(userPass) && null != userDetails) {

            nameRegister = findViewById(R.id.nameRegister);
            String updatedName = nameRegister.getText().toString().trim();

            passwordRegister = findViewById(R.id.passwordRegister);
            String updatedToken = passwordRegister.getText().toString();

            phoneRegister = findViewById(R.id.phoneRegister);
            String updatedPhone = phoneRegister.getText().toString();

            emailNotifications = findViewById(R.id.emailNotifications);
            boolean updatedEmails = emailNotifications.isChecked();

            acceptsNotifications = findViewById(R.id.acceptsNotifications);
            boolean updatedPushNotifications = acceptsNotifications.isChecked();

            UpdateAccountDetailsValidator updateAccountDetailsValidator = new UpdateAccountDetailsValidator();
            updateAccountDetailsValidator.setName(updatedName);
            updateAccountDetailsValidator.setLoginPassword(updatedToken);
            updateAccountDetailsValidator.setPhoneNumber(updatedPhone);
            try {
                updateAccountDetailsValidator.validate();
                UpdateAccountDetailsService updateAccountDetailsService = new UpdateAccountDetailsService();
                updateAccountDetailsService.setContext(this);
                updateAccountDetailsService.setEmail(userDetails.getUserEmail());
                updateAccountDetailsService.setName(updatedName);
                updateAccountDetailsService.setToken(updatedToken);
                updateAccountDetailsService.seOldToken(oldToken);
                updateAccountDetailsService.setPhoneNum(updatedPhone);
                updateAccountDetailsService.setEmailNotification(updatedEmails);
                updateAccountDetailsService.setPushNotification(updatedPushNotifications);
                updateAccountDetailsService.update();
            } catch (Exception e) {
                handleException(e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getInstance().unregister(this);
    }

    @Subscribe
    public void asyncDone(UserDetails message) {
        //TO MAKE proceedUpdateDetails WORK correctly
        this.userDetails = message;
    }

    private void handleException(Exception e) {
        if (e.getMessage().startsWith("Name")) {
            nameRegister.setError(e.getMessage());
        } else if (e.getMessage().startsWith("Password")) {
            passwordRegister.setError(e.getMessage());
        } else if (e.getMessage().startsWith("Phone")) {
            phoneRegister.setError(e.getMessage());
        } else {
            AlertUtils.showSimpleProblemSnackBar(AccountDetailsActivity.this, nameRegister, e.getMessage());
        }
    }
}