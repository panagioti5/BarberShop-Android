package com.barber.app.services;

import com.barber.app.DAOImpl.shops.ShopsGenericImpl;
import com.barber.app.entities.Shop;

import java.net.URL;
import java.util.List;

public class GetAPIGenericService extends Service
{
    protected ShopsGenericImpl shop;
    protected List<Shop> shopOutput;
    protected int shopIndexID;
    protected URL url;

    public GetAPIGenericService()
    {
        shop = new ShopsGenericImpl();
    }

    public List<Shop> getRESTOutput() {
        return shopOutput;
    }

    public int getRankIndex() {
        return shopIndexID;
    }

    @Override
    public void setRankIndex(int id) {

    }

    public void setURL(URL url)
    {
        this.url = url;
    }

    protected void setShopOutput(List<Shop> shopOutput) {
        this.shopOutput = shopOutput;
    }

    @Override
    protected void validateInput() throws Exception {

    }

    public void execute() throws Exception {
        setShopOutput(shop.getShopsGenericAPI(url));
    }
}

