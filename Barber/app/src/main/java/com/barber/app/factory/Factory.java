package com.barber.app.factory;

import com.barber.app.DAOImpl.shops.PopularShopsImpl;
import com.barber.app.DAOImpl.shops.SelectedShopImpl;
import com.barber.app.dao.ShopDAO;
import com.barber.app.DAOImpl.shops.CityShopsImpl;

public class Factory {


    public ShopDAO getShopFactory(String cityName) {
        if (cityName == null) {
            return null;
        }
        return new CityShopsImpl();
    }

    public ShopDAO getShopByIDFactory() {
        return new SelectedShopImpl();
    }

    public ShopDAO getPopularShops() {
        return new PopularShopsImpl();
    }
}
