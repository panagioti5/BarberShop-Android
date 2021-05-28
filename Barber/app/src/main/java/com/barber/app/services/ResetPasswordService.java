package com.barber.app.services;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.barber.app.DAOImpl.users.UserImpl;
import com.barber.app.ForgotPasswordActivity2Reset;
import com.barber.app.LoginActivity;
import com.barber.app.R;
import com.barber.app.constants.Constants;
import com.barber.app.dao.UserDAO;
import com.barber.app.utils.AlertUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wang.avi.AVLoadingIndicatorView;

public class ResetPasswordService {

    private ForgotPasswordActivity2Reset context;
    private String userEmail;
    private String password;
    private AVLoadingIndicatorView avi;
    private String callingActivity;
    private String code;

    public void setContext(ForgotPasswordActivity2Reset activity) {
        this.context = activity;
    }

    public void setNewToken(String token) {
        this.password = token;
    }

    public ForgotPasswordActivity2Reset getContext() {
        return context;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getPassword() {
        return password;
    }

    public AVLoadingIndicatorView getAvi() {
        return avi;
    }

    public String getCallingActivity() {
        return callingActivity;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void setCallingActivity(String callingActivity) {
        this.callingActivity = callingActivity;
    }

    public void reset() {
        RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlFpwTwo);
        enableDisableView(myLayout, false);
        avi = (AVLoadingIndicatorView) getContext().findViewById(R.id.avi);
        String indicator = getContext().getIntent().getStringExtra(Constants.INDICATOR);
        avi.setIndicator(indicator);
        avi.setVisibility(View.VISIBLE);
        avi.show();
        callResetToken(getContext());
    }


    private void callResetToken(ForgotPasswordActivity2Reset context) {
        context.isResetAccountInProgress = true;
        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    UserDAO userInterface = new UserImpl();
                    userInterface.resetPass(getContext(), getCode(), getUserEmail(), getPassword());
                } catch (Exception e) {
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                context.isResetAccountInProgress = false;
                RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlFpwTwo);
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


    private void enableDisableView(View view, boolean enabled) {
        view.setClickable(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    private void goToLoginScreenAfterSuccessfullReset() {
        Intent homepage = new Intent(getContext(), LoginActivity.class);
        if (null != getCallingActivity() && getCallingActivity().equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            homepage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, "BOOK_APPOINTMENT_ACTIVITY");
        } else {
            homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        getContext().startActivity(homepage);
        getContext().finish();
    }

    private void showConfirmAccountDialog() {
        new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme)
                .setCancelable(false)
                .setTitle("Your password has been reset.")
                .setMessage("You will now redirected to login page")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToLoginScreenAfterSuccessfullReset();
                    }
                }).show();
    }

    public void setResetCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
