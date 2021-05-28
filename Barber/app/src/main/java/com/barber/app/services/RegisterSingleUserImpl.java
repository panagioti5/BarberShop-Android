package com.barber.app.services;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.barber.app.DAOImpl.users.UserImpl;
import com.barber.app.LoginActivity;
import com.barber.app.R;
import com.barber.app.RegisterActivity;
import com.barber.app.constants.Constants;
import com.barber.app.dao.UserDAO;
import com.barber.app.utils.AlertUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wang.avi.AVLoadingIndicatorView;

public class RegisterSingleUserImpl {
    private RegisterActivity context;
    private String userEmail;
    private String password;
    private String userName;
    private String userPhoneNumber;
    private AVLoadingIndicatorView avi;
    private String callingActivity;
    private boolean emailNotifications;
    private boolean pushNotifications;

    public boolean isPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public boolean isEmailNotificationsEnabled() {
        return emailNotifications;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumbr) {
        this.userPhoneNumber = userPhoneNumbr;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setContext(RegisterActivity registerActivity) {
        this.context = registerActivity;
    }

    public RegisterActivity getContext() {
        return context;
    }

    public void register() {
        context.registeringInProgress = true;
        RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlRegister);
        enableDisableView(myLayout, false);
        avi = (AVLoadingIndicatorView) getContext().findViewById(R.id.avi);
        String indicator = getContext().getIntent().getStringExtra(Constants.INDICATOR);
        avi.setIndicator(indicator);
        avi.setVisibility(View.VISIBLE);
        avi.show();
        callCreateLogin(getContext());
    }

    private void callCreateLogin(RegisterActivity context) {
        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    UserDAO userInterface = new UserImpl();
                    userInterface.register(getContext(), getUserEmail(), getPassword(), getUserName(), getUserPhoneNumber(), isEmailNotificationsEnabled(), isPushNotifications());
                } catch (Exception e) {
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                context.registeringInProgress = false;
                RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlRegister);
                enableDisableView(myLayout, true);
                avi.hide();
                avi.setVisibility(View.INVISIBLE);
                if (s != null) {
                    AlertUtils.showSimpleProblemSnackBar(context, avi, s);
                } else {
                    showConfirmAccountDialog();
                }
            }
        };
        asyncTask.execute();
    }

    private void goToLoginScreenAfterSuccessfullLogin() {
        Intent homepage = new Intent(getContext(), LoginActivity.class);
        if (null != getCallingActivity() && getCallingActivity().equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            homepage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, "BOOK_APPOINTMENT_ACTIVITY");
        } else {
            homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        getContext().startActivity(homepage);
        getContext().finish();
    }

    public void setLoginEmail(String trim) {
        this.userEmail = trim;
    }

    public void setCallingActivity(String callingActivity) {
        this.callingActivity = callingActivity;
    }

    public String getCallingActivity() {
        return callingActivity;
    }

    private void showConfirmAccountDialog() {
        new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme)
                .setCancelable(false)
                .setTitle("Registered Successfully")
                .setMessage("We have sent you a mail. Go check it out to verify your account and complete your registration.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToLoginScreenAfterSuccessfullLogin();
                    }
                })
                .show();
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

    public void setEmailNotificationsEnabled(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public void setPushNotificationsEnabled(boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
}
