package com.barber.app.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.barber.app.DAOImpl.users.UserImpl;
import com.barber.app.LoginActivity;
import com.barber.app.R;
import com.barber.app.constants.Constants;
import com.barber.app.dao.UserDAO;
import com.barber.app.utils.AlertUtils;
import com.wang.avi.AVLoadingIndicatorView;

public class LogInSingleUserProblemResolverImpl {
    private Activity context;
    private String userEmail;
    private AVLoadingIndicatorView avi;
    private String callingActivity;


    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setContext(Activity loginActivity) {
        this.context = loginActivity;
    }

    public Activity getContext() {
        return context;
    }

    public void resolve() {
        RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlLoginProblem);
        enableDisableView(myLayout, false);
        avi = (AVLoadingIndicatorView) getContext().findViewById(R.id.avi);
        String indicator = getContext().getIntent().getStringExtra(Constants.INDICATOR);
        avi.setIndicator(indicator);
        avi.setVisibility(View.VISIBLE);
        avi.show();
        callResolveLostMail(getContext());
    }

    private void callResolveLostMail(Activity context) {

        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    UserDAO userInterface = new UserImpl();
                    userInterface.resolveLostMail(getContext(), getUserEmail());
                } catch (Exception e) {
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlLoginProblem);
                enableDisableView(myLayout, true);

                avi.hide();
                avi.setVisibility(View.INVISIBLE);

                if (s != null) {
                    AlertUtils.showSimpleProblemSnackBar(context, avi, s);
                } else {
                    goToLoginPageAfterSendingMail();
                }
            }
        };
        asyncTask.execute();
    }


    private void goToLoginPageAfterSendingMail() {
        Intent loginActivity = new Intent(getContext(), LoginActivity.class);
        if (null != getCallingActivity() && getCallingActivity().equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            loginActivity.putExtra(Constants.EXTRA_SESSION_COMING_ACTIVITY, "BOOK_APPOINTMENT_ACTIVITY");
        } else {
            loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        getContext().startActivity(loginActivity);
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
