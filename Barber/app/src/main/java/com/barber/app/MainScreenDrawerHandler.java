package com.barber.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import com.barber.app.constants.Constants;
import com.barber.app.services.LogOutSingleUser;
import com.barber.app.utils.AlertUtils;
import com.barber.app.utils.UtilsImpl;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;

public class MainScreenDrawerHandler {


    private final MainActivity context;

    public MainScreenDrawerHandler(MainActivity context) {
        this.context = context;
    }

    public void handleUIDrawer() {
        try {
            Menu menu = context.navigationView.getMenu();
            MenuItem favs = menu.findItem(R.id.favorites);
            favs.getIcon().setColorFilter(Color.parseColor("#F8D712"), PorterDuff.Mode.SRC_ATOP);
        } catch (Exception e) {

        }
    }


    public void handleDrawer() {
        context.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent newsPage = new Intent(context, MainShopsActivity.class);
                newsPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                if (menuItem.getItemId() == R.id.nav_nicosia) {
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, Constants.TITLE_NICOSIA);
                    context.startActivity(newsPage);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_limassol) {
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, Constants.TITLE_LIMASSOL);
                    context.startActivity(newsPage);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_larnaca) {
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, Constants.TITLE_LARNACA);
                    context.startActivity(newsPage);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_paphos) {
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, Constants.TITLE_PAPHOS);
                    context.startActivity(newsPage);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_ammochostos) {
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, Constants.TITLE_AMMOCHOSTOS);
                    context.startActivity(newsPage);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_login) {
                    if (!UtilsImpl.isUserSignedIn(context)) {
                        Intent loginPage = new Intent(context, LoginActivity.class);
                        loginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(loginPage);
                    }
                    return true;
                }

                if (menuItem.getItemId() == R.id.nav_settings) {
                    if (UtilsImpl.isUserSignedIn(context)) {
                        Intent loginPage = new Intent(context, AccountDetailsActivity.class);
                        loginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(loginPage);
                    }
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_requests) {
                    if (!UtilsImpl.isNetworkAvailable(context)) {
                        AlertUtils.showSimpleAlert(context, "No Network", "Please connect to network", "OK");
                    } else if (!UtilsImpl.isUserSignedIn(context)) {
                        AlertUtils.showSimpleAlert(context, "Sign In", "Sign in or create account to manage your requests", "OK");
                    } else {
                        //todo change activity
                    }
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_share) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Hey, check out Barber Shops app at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_send) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "support@devteam.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Barber Shops App");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    context.startActivity(Intent.createChooser(emailIntent, "Send email"));
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_rate) {
                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        context.startActivity(goToMarket);
                    } catch (Exception e) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                    }
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_privacy_policy) {
                    Intent privacyPolicy = new Intent(context, PrivacyPolicyActivity.class);
                    context.startActivity(privacyPolicy);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_data_protection) {
                    Intent privacyPolicy = new Intent(context, DataProtectionActivity.class);
                    context.startActivity(privacyPolicy);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_logout) {
                    LogOutSingleUser logOutSingleUser = new LogOutSingleUser(context);
                    logOutSingleUser.logout();
                    return true;
                }
                if (menuItem.getItemId() == R.id.favorites) {
                    Intent loginPage = new Intent(context, FavoritesActivity.class);
                    loginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(loginPage);
                    return true;
                }
                return true;
            }
        });

    }
}
