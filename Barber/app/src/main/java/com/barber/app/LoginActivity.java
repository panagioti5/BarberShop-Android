package com.barber.app;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.barber.app.constants.Constants;
import com.barber.app.services.LogInSingleUserImpl;
import com.barber.app.utils.AlertUtils;
import com.barber.app.validators.LoginValidator;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    private EditText userEmail;
    private EditText userPassword;
    private Button problemLogIn;
    private String callingActivity;
    private final String activityTitle = "Log In";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(activityTitle);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        problemLogIn = findViewById(R.id.problemLogIn);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            callingActivity = extras.getString(Constants.EXTRA_SESSION_COMING_ACTIVITY);
        }
        problemLogIn.setPaintFlags(problemLogIn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
                    finish();
                    return true;
                } else {
//                    Intent homepage = new Intent(LoginActivity.this, MainActivity.class);
//                    homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(homepage);
                    finish();
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
                finish();
                return true;
            } else {
//                Intent homepage = new Intent(LoginActivity.this, MainActivity.class);
//                homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(homepage);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void cancelLogin(View v) {
        if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            finish();
        } else {
//            Intent homepage = new Intent(LoginActivity.this, MainActivity.class);
//            homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(homepage);
            finish();
        }
    }

    public void registerAccount(View v) {
        Intent registerPage = new Intent(LoginActivity.this, RegisterActivity.class);
        if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            registerPage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, Constants.BOOK_APPOINTMENT_ACTIVITY);
        } else {
            registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(registerPage);
        finish();
    }

    public void forgotPassword(View v) {
        Intent forgotPasswordPage = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            forgotPasswordPage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, Constants.BOOK_APPOINTMENT_ACTIVITY);
        } else {
            forgotPasswordPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(forgotPasswordPage);
        finish();
    }

    public void proceedLogin(View v) {
        String email = userEmail.getText().toString().toLowerCase().trim();
        String token = userPassword.getText().toString();

        LoginValidator loginValidator = new LoginValidator();
        loginValidator.setLoginEmail(email);
        loginValidator.setLoginPassword(token);
        loginValidator.setContext(this);
        try {
            loginValidator.validate();
            LogInSingleUserImpl logInSingleUser = new LogInSingleUserImpl();
            logInSingleUser.setContext(this);
            logInSingleUser.setUserEmail(email);
            logInSingleUser.setPassword(token);
            logInSingleUser.setCallingActivity(callingActivity);
            logInSingleUser.login();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (e.getMessage().startsWith("Email")) {
            userEmail.setError(e.getMessage());
        } else if (e.getMessage().startsWith("Password")) {
            userPassword.setError(e.getMessage());
        } else {
            AlertUtils.showSimpleProblemSnackBar(LoginActivity.this, userEmail, e.getMessage());
        }
    }

    public void problemLogIn(View v) {
        Intent problemLogInActivity = new Intent(LoginActivity.this, ProblemLogInActivity.class);
        if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            problemLogInActivity.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, Constants.BOOK_APPOINTMENT_ACTIVITY);
        } else {
            problemLogInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(problemLogInActivity);
        finish();
    }
}