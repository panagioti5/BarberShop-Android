package com.barber.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.barber.app.constants.Constants;
import com.barber.app.services.CallSendResetTokenService;
import com.barber.app.utils.AlertUtils;
import com.barber.app.validators.ForgotPasswordValidator;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText forgotPasswordEmail;
    private String callingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");
        forgotPasswordEmail = findViewById(R.id.forgotPasswordEmail);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            callingActivity = extras.getString(Constants.EXTRA_SESSION_COMING_ACTIVITY);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent homepage = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
                    homepage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, "BOOK_APPOINTMENT_ACTIVITY");
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
            Intent homepage = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
                homepage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, "BOOK_APPOINTMENT_ACTIVITY");
            }
            startActivity(homepage);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickNext(View v) {
        String email = forgotPasswordEmail.getText().toString().trim().toLowerCase();
        ForgotPasswordValidator forgotPasswordValidator = new ForgotPasswordValidator();
        forgotPasswordValidator.setEmail(email);
        try {
            forgotPasswordValidator.validate();
            CallSendResetTokenService callSendResetTokenService = new CallSendResetTokenService();
            callSendResetTokenService.setContext(this);
            callSendResetTokenService.setUserEmail(email);
            callSendResetTokenService.setCallingActivity(callingActivity);
            callSendResetTokenService.send();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (e.getMessage().startsWith("Email")) {
            forgotPasswordEmail.setError(e.getMessage());
        } else {
            AlertUtils.showSimpleProblemSnackBar(ForgotPasswordActivity.this, forgotPasswordEmail, e.getMessage());
        }
    }
}