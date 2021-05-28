package com.barber.app.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barber.app.AccountDetailsActivity;
import com.barber.app.DAOImpl.users.UserImpl;
import com.barber.app.R;
import com.barber.app.constants.Constants;
import com.barber.app.dao.EventBus;
import com.barber.app.dao.UserDAO;
import com.barber.app.entities.UserDetails;
import com.barber.app.utils.AlertUtils;
import com.barber.app.utils.EncryptorDecryptor;
import com.wang.avi.AVLoadingIndicatorView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class AccountDetailsBYMailService {
    private AccountDetailsActivity context;
    private String userEmail;
    private AVLoadingIndicatorView avi;
    private ImageView noInternetImg;
    private UserDetails userDetails;
    private SwipeRefreshLayout pullToRefresh;
    private EditText nameRegister;
    private TextView emailRegister;
    private EditText passwordRegister;
    private EditText phoneRegister;
    private CheckBox emailNotifications;
    private String userPass;
    private CheckBox pushNotifications;

    public String getUserPass() {
        return userPass;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setContext(AccountDetailsActivity loginActivity) {
        this.context = loginActivity;
    }

    public AccountDetailsActivity getContext() {
        return context;
    }

    public void retrieve() {
        nameRegister = getContext().findViewById(R.id.nameRegister);
        emailRegister = getContext().findViewById(R.id.emailRegister);
        passwordRegister = getContext().findViewById(R.id.passwordRegister);
        phoneRegister = getContext().findViewById(R.id.phoneRegister);
        pullToRefresh = getContext().findViewById(R.id.pullToRefresh);
        noInternetImg = getContext().findViewById(R.id.noInternetImg);
        emailNotifications = getContext().findViewById(R.id.emailNotifications);
        pushNotifications = getContext().findViewById(R.id.acceptsNotifications);
        avi = (AVLoadingIndicatorView) getContext().findViewById(R.id.avi);
        String indicator = getContext().getIntent().getStringExtra(Constants.INDICATOR);
        avi.setIndicator(indicator);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                avi.show();
                avi.setVisibility(View.VISIBLE);
                callRetrieveDetails(getContext());
                pullToRefresh.setRefreshing(false);
            }
        });
        callFirstLoad();
    }

    private void callFirstLoad() {
        hideAllScreenViews();
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
                    UserDAO userInterface = new UserImpl();
                    setUserDetails(userInterface.getUserDetails(getContext(), getUserEmail(), getUserPass()));

                    if (getUserDetails() == null) {
                        throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
                    }
                } catch (Exception e) {
                    getContext().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            pullToRefresh.setRefreshing(false);
                            avi.hide();
                            avi.setVisibility(View.INVISIBLE);
                            //CHECK FIRST LOAD OR REFRESH
                            if (getUserDetails() == null) {
                                //IN CASE WE VISITED THIS SCREEN WITH NO NET CONNECTION
                                hideAllScreenViews();
                                noInternetImg.setVisibility(View.VISIBLE);
                            } else if (userDetails != null && e.getMessage().equals(Constants.SOMETHING_WENT_WRONG_NET)) {
                                noInternetImg.setVisibility(View.INVISIBLE);
                            } else {
                                noInternetImg.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                avi.hide();
                avi.setVisibility(View.INVISIBLE);
                if (s != null) {
                    if (getUserDetails() == null) {
                        //IN CASE WE VISITED THIS SCREEN WITH NO NET CONNECTION
                        hideAllScreenViews();
                        avi.hide();
                        avi.setVisibility(View.INVISIBLE);
                        noInternetImg.setVisibility(View.VISIBLE);
                    } else if (getUserDetails() != null && s.equals(Constants.SOMETHING_WENT_WRONG_NET)) {
                        enableDisableView(myLayout, true);
                        noInternetImg.setVisibility(View.INVISIBLE);
                    } else {
                        noInternetImg.setVisibility(View.INVISIBLE);
                    }
                    AlertUtils.showSimpleProblemSnackBar(context, avi, s);
                    enableDisableView(myLayout, true);
                } else {
                    super.onPostExecute(s);
                    EventBus.getInstance().post(userDetails);
                    showAllScreenViews();
                    RelativeLayout myLayout = (RelativeLayout) getContext().findViewById(R.id.srlAccountDetails);
                    enableDisableView(myLayout, true);
                    addAttributeFieldsOnScreenComponents(getUserDetails());
                }
            }
        };
        asyncTask.execute();
    }

    private void addAttributeFieldsOnScreenComponents(UserDetails userDetails) {
        nameRegister.setText(userDetails.getName());
        emailRegister.setText(userDetails.getUserEmail());
        phoneRegister.setText(String.valueOf(userDetails.getPhoneNumber()));
        String decryptedPass = EncryptorDecryptor.decrypt(userDetails.getUserToken(), Constants.KEY);
        passwordRegister.setText(decryptedPass);
        emailNotifications.setChecked(userDetails.isAcceptsNotifications());
        pushNotifications.setChecked(userDetails.isAcceptsPushNotifications());
        getContext().oldToken = (decryptedPass);
    }

    private void showAllScreenViews() {
        RelativeLayout layout = (RelativeLayout) getContext().findViewById(R.id.srlAccountDetails);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setVisibility(View.VISIBLE);
            child.setClickable(true);
            child.setEnabled(true);
            if (child.getId() != R.id.emailRegister) {
                child.setClickable(false);
                child.setEnabled(false);
                continue;
            }
        }
        noInternetImg.setVisibility(View.INVISIBLE);
        avi.hide();
        avi.setVisibility(View.INVISIBLE);
    }

    private void hideAllScreenViews() {
        RelativeLayout layout = (RelativeLayout) getContext().findViewById(R.id.srlAccountDetails);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setVisibility(View.INVISIBLE);
        }
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

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}
