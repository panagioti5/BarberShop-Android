package com.barber.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.barber.app.constants.Constants;
import com.barber.app.services.RegisterSingleUserImpl;
import com.barber.app.utils.AlertUtils;
import com.barber.app.validators.RegisterValidator;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {


    public EditText nameRegister;
    public EditText emailRegister;
    public EditText passwordRegister;
    public EditText phoneRegister;
    private String callingActivity;
    private final String activityTitle = "Register";
    public CheckBox checkBoxAgree;
    private CheckBox emailNotifications;
    private CheckBox pushNotifications;
    public boolean registeringInProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(activityTitle);
        initializeScreenComponents();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            callingActivity = extras.getString(Constants.EXTRA_SESSION_COMING_ACTIVITY);
        }
        registeringInProgress = false;
    }

    private void initializeScreenComponents() {
        nameRegister = findViewById(R.id.nameRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        phoneRegister = findViewById(R.id.phoneRegister);
        checkBoxAgree = findViewById(R.id.checkBoxAgree);
        emailNotifications = findViewById(R.id.emailNotifications);
        pushNotifications = findViewById(R.id.acceptsPushNotifications);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!registeringInProgress) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent homepage = new Intent(RegisterActivity.this, LoginActivity.class);
                    homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
                        homepage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, Constants.BOOK_APPOINTMENT_ACTIVITY);
                    }
                    startActivity(homepage);
                    finish();
                    return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!registeringInProgress) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                Intent homepage = new Intent(RegisterActivity.this, LoginActivity.class);
                homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
                    homepage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, Constants.BOOK_APPOINTMENT_ACTIVITY);
                }
                startActivity(homepage);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void cancelRegister(View v) {
        Intent homepage = new Intent(RegisterActivity.this, LoginActivity.class);
        if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            homepage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, Constants.BOOK_APPOINTMENT_ACTIVITY);
        }
        homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homepage);
        finish();
    }

    public void proceedRegister(View v) {
        String name = nameRegister.getText().toString().trim();
        String email = emailRegister.getText().toString().trim().toLowerCase();
        String token = passwordRegister.getText().toString();
        String phone = phoneRegister.getText().toString().trim();

        RegisterValidator registerValidator = new RegisterValidator();
        registerValidator.setContext(this);
        registerValidator.setName(name);
        registerValidator.setEmail(email);
        registerValidator.setToken(token);
        registerValidator.setPhone(phone);
        registerValidator.setAgree(checkBoxAgree.isChecked());
        try {
            registerValidator.validate();
            RegisterSingleUserImpl registerSingleUser = new RegisterSingleUserImpl();
            registerSingleUser.setContext(this);
            registerSingleUser.setLoginEmail(email);
            registerSingleUser.setPassword(token);
            registerSingleUser.setUserName(name);
            registerSingleUser.setUserPhoneNumber(phone);
            registerSingleUser.setEmailNotificationsEnabled(emailNotifications.isChecked());
            registerSingleUser.setPushNotificationsEnabled(pushNotifications.isChecked());
            registerSingleUser.setCallingActivity(callingActivity);
            registerSingleUser.register();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (e.getMessage().startsWith("Name")) {
            nameRegister.setError(e.getMessage());
        } else if (e.getMessage().startsWith("Email")) {
            emailRegister.setError(e.getMessage());
        } else if (e.getMessage().startsWith("Password")) {
            passwordRegister.setError(e.getMessage());
        } else if (e.getMessage().startsWith("Phone")) {
            phoneRegister.setError(e.getMessage());
        } else if (e.getMessage().startsWith("Please")) {
            checkBoxAgree.setError(e.getMessage());
        } else {
            AlertUtils.showSimpleProblemSnackBar(RegisterActivity.this, nameRegister, e.getMessage());
        }
    }
}