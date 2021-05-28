package com.barber.app.DAOImpl.shops;

import android.content.Context;
import android.content.res.AssetManager;

import com.barber.app.constants.Constants;
import com.barber.app.dao.ShopDAO;
import com.barber.app.entities.Shop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PopularShopsImpl implements ShopDAO {
    @Override
    public List<Shop> getAllShopsByArea(Context context, String shopCity, int i) throws Exception {
        return null;
    }

    @Override
    public Shop getSelectedShopByID(Context context, long i) throws Exception {
        return null;
    }

    @Override
    public List<Shop> getAllPopularShops(Context context) throws Exception {
        Properties properties = new Properties();

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);

        List<Shop> shopsLocal;
        try {
            shopsLocal = new ArrayList<>();
            URL url = new URL(properties.getProperty("url") + properties.getProperty("popularShops"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_GET);
            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE) {
                throw new Exception(Constants.BAD_REQUEST);
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output = Constants.EMPTY_STRING;
            StringBuilder sb = new StringBuilder();

            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            if (sb == null || sb.toString().isEmpty()) {
                throw new Exception(Constants.NOTHING_TO_SHOW);
            }
            shopsLocal = new Gson().fromJson(sb.toString(), new TypeToken<List<Shop>>() {
            }.getType());

            conn.disconnect();
        } catch (Exception e) {
            if (e.getMessage().equals(Constants.BAD_REQUEST)) {
                throw new Exception(Constants.BAD_REQUEST);
            }
            if (e.getMessage().equals(Constants.NOTHING_TO_SHOW)) {
                throw new Exception(Constants.NOTHING_TO_SHOW);
            }
            if (e.getMessage().equals(Constants.NETWORK_UNAVAILABLE)) {
                throw new Exception(Constants.NETWORK_UNAVAILABLE);
            }
            throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
        }
        return shopsLocal;
    }
}
