package com.barber.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.barber.app.constants.Constants;
import com.barber.app.services.LogInSingleUserProblemResolverImpl;
import com.barber.app.utils.AlertUtils;
import com.barber.app.validators.EmailValidator;

import androidx.appcompat.app.AppCompatActivity;

public class ProblemLogInActivity extends AppCompatActivity {

    private String callingActivity;
    private final String activityTitle = "Verification Email";
    private EditText userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_log_in);
        Bundle extras = getIntent().getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(activityTitle);
        if (extras != null) {
            callingActivity = extras.getString(Constants.EXTRA_SESSION_COMING_ACTIVITY);
        }
        userEmail = findViewById(R.id.userEmail);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent homepage = new Intent(ProblemLogInActivity.this, LoginActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent homepage = new Intent(ProblemLogInActivity.this, LoginActivity.class);
            homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
                homepage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, Constants.BOOK_APPOINTMENT_ACTIVITY);
            }
            startActivity(homepage);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void supportPhoneClick(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:99084676"));
        startActivity(intent);
    }

    public void callSendProblemLogIn(View v) {
        String email = userEmail.getText().toString().trim().toLowerCase();
        EmailValidator emailValidator = new EmailValidator();
        emailValidator.setMail(email);
        try {
            emailValidator.validate();
            LogInSingleUserProblemResolverImpl logInSingleUserProblemResolver = new LogInSingleUserProblemResolverImpl();
            logInSingleUserProblemResolver.setContext(this);
            logInSingleUserProblemResolver.setUserEmail(email);
            logInSingleUserProblemResolver.setCallingActivity(callingActivity);
            logInSingleUserProblemResolver.resolve();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (e.getMessage().startsWith("Email")) {
            userEmail.setError(e.getMessage());
        } else {
            AlertUtils.showSimpleProblemSnackBar(ProblemLogInActivity.this, userEmail, e.getMessage());
        }
    }

    public void problemLogInMailUs(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@devteam.com" +
                "?subject=Problem in Log In"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi there, i face some issues while trying to login. Please check this out." + "");
        startActivity(Intent.createChooser(emailIntent, "Select your mail account to send mail"));
    }
}