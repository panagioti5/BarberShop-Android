package com.barber.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.barber.app.constants.Constants;
import com.barber.app.services.ResetPasswordService;
import com.barber.app.utils.AlertUtils;
import com.barber.app.validators.UpdatePasswordValidator;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity2Reset extends AppCompatActivity {
    private EditText resetCode;
    private EditText newPassword;
    private String callingActivity;
    private String emailToReset;
    public boolean isResetAccountInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_activity2_reset);
        resetCode = findViewById(R.id.resetCode);
        newPassword = findViewById(R.id.newPassword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            callingActivity = extras.getString(Constants.EXTRA_SESSION_COMING_ACTIVITY);
            emailToReset = extras.getString("EMAIL_TO_RESET");
        }
        isResetAccountInProgress = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!isResetAccountInProgress) {
            if (id == android.R.id.home) {
                Intent forgotPasswordActivity = new Intent(ForgotPasswordActivity2Reset.this, ForgotPasswordActivity.class);
                if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
                    forgotPasswordActivity.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, "BOOK_APPOINTMENT_ACTIVITY");
                }
                startActivity(forgotPasswordActivity);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isResetAccountInProgress) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent forgotPasswordActivity = new Intent(ForgotPasswordActivity2Reset.this, ForgotPasswordActivity.class);
                    forgotPasswordActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
                        forgotPasswordActivity.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, "BOOK_APPOINTMENT_ACTIVITY");
                    }
                    startActivity(forgotPasswordActivity);
                    finish();
                    return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    public void resetPassword(View v) {
        String newToken = newPassword.getText().toString();
        String code = resetCode.getText().toString().trim();

        UpdatePasswordValidator updatePasswordValidator = new UpdatePasswordValidator();
        updatePasswordValidator.setResetCode(code);
        updatePasswordValidator.setNewToken(newToken);
        try {
            updatePasswordValidator.validate();
            ResetPasswordService resetPasswordService = new ResetPasswordService();
            resetPasswordService.setContext(this);
            resetPasswordService.setUserEmail(emailToReset);
            resetPasswordService.setNewToken(newToken);
            resetPasswordService.setResetCode(code);
            resetPasswordService.setCallingActivity(callingActivity);
            resetPasswordService.reset();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (e.getMessage().startsWith("Reset")) {
            resetCode.setError(e.getMessage());
        } else if (e.getMessage().startsWith("Password")) {
            newPassword.setError(e.getMessage());
        } else {
            AlertUtils.showSimpleProblemSnackBar(ForgotPasswordActivity2Reset.this, resetCode, e.getMessage());
        }
    }
}