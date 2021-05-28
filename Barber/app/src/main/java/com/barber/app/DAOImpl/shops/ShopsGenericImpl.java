package com.barber.app.DAOImpl.shops;

import com.barber.app.constants.Constants;
import com.barber.app.dao.ShopDAO;
import com.barber.app.entities.Shop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShopsGenericImpl
{
    public List<Shop> getShopsGenericAPI(URL url) throws Exception
    {
        List<Shop> shopsLocal;
        try
        {
            shopsLocal = new ArrayList<>();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_GET);
            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE)
            {
                throw new Exception(Constants.BAD_REQUEST);
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output = Constants.EMPTY_STRING;
            StringBuilder sb = new StringBuilder();

            while ((output = br.readLine()) != null)
            {
                sb.append(output);
            }

            if (sb == null || sb.toString().isEmpty())
            {
                throw new Exception(Constants.NOTHING_TO_SHOW);
            }
            shopsLocal = new Gson().fromJson(sb.toString(), new TypeToken<List<Shop>>()
            {
            }.getType());

            conn.disconnect();
        } catch (Exception e)
        {
            e.printStackTrace();
            if (e.getMessage().equals(Constants.BAD_REQUEST))
            {
                throw new Exception(Constants.BAD_REQUEST);
            }
            if (e.getMessage().equals(Constants.NOTHING_TO_SHOW))
            {
                throw new Exception(Constants.NOTHING_TO_SHOW);
            }
            if (e.getMessage().equals(Constants.NETWORK_UNAVAILABLE))
            {
                throw new Exception(Constants.NETWORK_UNAVAILABLE);
            }
            throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
        }
        return shopsLocal;
    }


}
