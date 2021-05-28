package com.barber.app.services;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;

import com.barber.app.DAOImpl.firetoken.FireTokenDaoImpl;
import com.barber.app.MainActivity;
import com.barber.app.R;
import com.barber.app.constants.Constants;
import com.barber.app.dao.FireTokenDaoIntf;
import com.barber.app.utils.AlertUtils;
import com.wang.avi.AVLoadingIndicatorView;

import static android.content.Context.MODE_PRIVATE;

public class LogOutSingleUser {


    private final MainActivity context;
    private AVLoadingIndicatorView avi;

    public LogOutSingleUser(MainActivity context) {
        this.context = context;
    }

    public void logout() {
        avi = (AVLoadingIndicatorView) context.findViewById(R.id.avi);
        String indicator = context.getIntent().getStringExtra(Constants.INDICATOR);
        avi.setIndicator(indicator);
        avi.setVisibility(View.VISIBLE);
        avi.show();
        @SuppressLint("StaticFieldLeak") final AsyncTask<Integer, String, String> asyncTask = new AsyncTask<Integer, String, String>() {
            @Override
            protected String doInBackground(Integer... newsIDs) {
                try {
                    FireTokenDaoIntf tokenToDB = new FireTokenDaoImpl(context, true, false);
                    tokenToDB.register();
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
                    AlertUtils.showNetworkProblemSnackBar(context, avi, Constants.SOMETHING_WENT_WRONG_NET);
                } else {
                    SharedPreferences preferences = context.getSharedPreferences(Constants.SHOP_PACK, MODE_PRIVATE);
                    preferences.edit().remove(Constants.IS_CONNECTED).commit();
                    context.finish();
                    context.startActivity(context.getIntent());
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
}
