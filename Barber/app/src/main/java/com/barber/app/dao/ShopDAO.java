package com.barber.app.dao;

import android.content.Context;

import com.barber.app.entities.Shop;

import java.util.List;

public interface ShopDAO {

    public List<Shop> getAllShopsByArea(Context context, String shopCity, int i) throws Exception;

    public Shop getSelectedShopByID(Context context, long i) throws Exception;

    public List<Shop> getAllPopularShops(Context context) throws Exception;
}
