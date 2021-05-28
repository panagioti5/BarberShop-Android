package com.barber.app.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.barber.app.DAOImpl.users.UserImpl;
import com.barber.app.R;
import com.barber.app.constants.Constants;
import com.barber.app.dao.UserDAO;
import com.barber.app.entities.UserDetails;
import com.barber.app.utils.AlertUtils;
import com.wang.avi.AVLoadingIndicatorView;

import static android.content.Context.MODE_PRIVATE;

public class UpdateAccountDetailsService {


    private Activity context;
    private String mail;
    private String name;
    private String token;
    private String phoneNum;
    private boolean emailNotification;
    AVLoadingIndicatorView avi;
    private boolean pushNotification;
    private String oldToken;


    public boolean isPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(boolean pushNotification) {
        this.pushNotification = pushNotification;
    }

    public Activity getContext() {
        return context;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public boolean isEmailNotification() {
        return emailNotification;
    }

    public void setContext(Activity accountDetailsActivity) {
        this.context = accountDetailsActivity;
    }

    public void setEmail(String mail) {
        this.mail = mail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setEmailNotification(boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    public void update() {
        avi = (AVLoadingIndicatorView) getContext().findViewById(R.id.avi);
        String indicator = getContext().getIntent().getStringExtra(Constants.INDICATOR);
        avi.setIndicator(indicator);
        callFirstUpdate();
    }

    private void callFirstUpdate() {
        avi.show();
        avi.setVisibility(View.VISIBLE);
        callRetrieveDetails(getContext());
    }

    private void callRetrieveDetails(Activity context) {
        RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlAccountDetails);
        enableDisableView(myLayout, false);
        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    UserDetails userDetails = new UserDetails();
                    userDetails.setAcceptsNotifications(isEmailNotification());
                    userDetails.setName(getName());
                    userDetails.setPhoneNumber(Long.parseLong(getPhoneNum()));
                    userDetails.setUserEmail(getMail());
                    userDetails.setAcceptsPushNotifications(isPushNotification());
                    userDetails.setUserToken(getToken());
                    userDetails.setUserOldToken(getOldToken());
                    UserDAO userInterface = new UserImpl();
                    userInterface.updateDetails(getContext(), userDetails);
                } catch (Exception e) {
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                avi.hide();
                avi.setVisibility(View.INVISIBLE);
                if (s != null) {
                    avi.hide();
                    avi.setVisibility(View.INVISIBLE);
                    AlertUtils.showSimpleProblemSnackBar(context, avi, s);
                    enableDisableView(myLayout, true);
                } else {
                    AlertUtils.showSimpleProblemSnackBar(context, avi, "Details Updated");
                    SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHOP_PACK, MODE_PRIVATE).edit();
                    editor.putString(Constants.CONNECTED_ACCOUNT_PWD, getToken().toString());
                    editor.apply();
                    enableDisableView(myLayout, true);
                }
            }
        };
        asyncTask.execute();
    }

    private void enableDisableView(View view, boolean enabled) {
        view.setClickable(enabled);
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    public String getOldToken() {
        return oldToken;
    }

    public void seOldToken(String oldToken) {
        this.oldToken = oldToken;
    }
}
