package com.barber.app.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.barber.app.DAOImpl.firetoken.FireTokenDaoImpl;
import com.barber.app.DAOImpl.users.UserImpl;
import com.barber.app.MainActivity;
import com.barber.app.R;
import com.barber.app.constants.Constants;
import com.barber.app.dao.FireTokenDaoIntf;
import com.barber.app.dao.UserDAO;
import com.barber.app.utils.AlertUtils;
import com.wang.avi.AVLoadingIndicatorView;

import static android.content.Context.MODE_PRIVATE;

public class LogInSingleUserImpl {
    private Activity context;
    private String userEmail;
    private String password;
    private AVLoadingIndicatorView avi;
    private String callingActivity;

    public String getUserEmail() {
        return userEmail;
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

    public void setContext(Activity loginActivity) {
        this.context = loginActivity;
    }

    public Activity getContext() {
        return context;
    }

    public void login() {
        RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlLogin);
        enableDisableView(myLayout, false);
        avi = (AVLoadingIndicatorView) getContext().findViewById(R.id.avi);
        String indicator = getContext().getIntent().getStringExtra(Constants.INDICATOR);
        avi.setIndicator(indicator);
        avi.setVisibility(View.VISIBLE);
        avi.show();
        callCreateLogin(getContext());
    }

    private void callCreateLogin(Activity context) {

        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    UserDAO userInterface = new UserImpl();
                    userInterface.login(getContext(), getUserEmail(), getPassword());
                } catch (Exception e) {
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlLogin);
                enableDisableView(myLayout, true);

                avi.hide();
                avi.setVisibility(View.INVISIBLE);

                if (s != null) {
                    AlertUtils.showSimpleProblemSnackBar(context, avi, s);
                } else {
                    SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHOP_PACK, MODE_PRIVATE).edit();
                    editor.putString(Constants.IS_CONNECTED, Constants.CONNECTED);
                    editor.putString(Constants.CONNECTED_ACCOUNT, getUserEmail().toString().toLowerCase());
                    editor.putString(Constants.CONNECTED_ACCOUNT_PWD, getPassword().toString());
                    editor.apply();
                    registerterFirebaseTokenOnAccount();
                    goToMainScreenAfterSuccessfullLogin();
                }
            }
        };
        asyncTask.execute();
    }

    private void registerterFirebaseTokenOnAccount() {
        try {
            FireTokenDaoIntf tokenToDB = new FireTokenDaoImpl(getContext(), true, true);
            tokenToDB.register();
        } catch (Exception e) {
        }
    }


    private void goToMainScreenAfterSuccessfullLogin() {
        Intent homepage = new Intent(getContext(), MainActivity.class);
        if (null != getCallingActivity() && getCallingActivity().equals(Constants.BOOK_APPOINTMENT_ACTIVITY)) {
            getContext().finish();
        } else {
            homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(homepage);
            getContext().finish();
        }
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
