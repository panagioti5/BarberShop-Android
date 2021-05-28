package com.barber.app.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.barber.app.DAOImpl.users.UserImpl;
import com.barber.app.ForgotPasswordActivity2Reset;
import com.barber.app.R;
import com.barber.app.constants.Constants;
import com.barber.app.dao.UserDAO;
import com.barber.app.utils.AlertUtils;
import com.wang.avi.AVLoadingIndicatorView;

public class CallSendResetTokenService {
    private Activity context;
    private String userEmail;
    private AVLoadingIndicatorView avi;
    private String callingActivity;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public void setContext(Activity loginActivity) {
        this.context = loginActivity;
    }

    public Activity getContext() {
        return context;
    }

    public void send() {
        RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlForgotPwd);
        enableDisableView(myLayout, false);
        avi = (AVLoadingIndicatorView) getContext().findViewById(R.id.avi);
        String indicator = getContext().getIntent().getStringExtra(Constants.INDICATOR);
        avi.setIndicator(indicator);
        avi.setVisibility(View.VISIBLE);
        avi.show();
        callSendMail(getContext());
    }

    private void callSendMail(Activity context) {

        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    UserDAO userInterface = new UserImpl();
                    userInterface.sendResetMail(getContext(), getUserEmail());
                } catch (Exception e) {
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlForgotPwd);
                enableDisableView(myLayout, true);

                avi.hide();
                avi.setVisibility(View.INVISIBLE);

                if (s != null) {
                    AlertUtils.showSimpleProblemSnackBar(context, avi, s);
                } else {
                    goToReset2Page();
                }
            }
        };
        asyncTask.execute();
    }

    private void goToReset2Page() {
        Intent forgotPasswordPage = new Intent(getContext(), ForgotPasswordActivity2Reset.class);
        if (null != callingActivity && callingActivity.equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            forgotPasswordPage.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, Constants.BOOK_APPOINTMENT_ACTIVITY);
            forgotPasswordPage.putExtra("EMAIL_TO_RESET", getUserEmail());
        } else {
            forgotPasswordPage.putExtra("EMAIL_TO_RESET", getUserEmail());
            forgotPasswordPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        getContext().startActivity(forgotPasswordPage);
        getContext().finish();
    }

    public void setCallingActivity(String callingActivity) {
        this.callingActivity = callingActivity;
    }

    public String getCallingActivity() {
        return callingActivity;
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
}
