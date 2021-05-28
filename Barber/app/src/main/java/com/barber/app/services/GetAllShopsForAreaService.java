package com.barber.app.services;

import android.content.Context;

import com.barber.app.dao.ShopDAO;
import com.barber.app.factory.Factory;
import com.barber.app.entities.Shop;
import com.barber.app.constants.Constants;

import java.util.List;

public class GetAllShopsForAreaService extends Service {

    protected ShopDAO barberIntf = null;
    protected List<Shop> shopOutput;
    protected int shopIndexID;
    protected String shopArea;
    protected Context appContect;

    public GetAllShopsForAreaService(Context context, String shopArea) {
        appContect = context;
        this.shopArea = shopArea;
        barberIntf = new Factory().getShopFactory(shopArea);
    }

    public List<Shop> getRESTOutput() {
        return shopOutput;
    }

    public int getRankIndex() {
        return shopIndexID;
    }


    @Override
    public void setRankIndex(int id) {
        shopIndexID = id;
    }

    protected void setShopOutput(List<Shop> shopOutput) {
        this.shopOutput = shopOutput;
    }

    @Override
    protected void validateInput() throws Exception {
        if (this.getRankIndex() == -1) {
            throw new Exception(Constants.SOMETHING_WENT_WRONG);
        }
    }

    public void execute() throws Exception {
        this.validateInput();
        if (barberIntf == null) {
            throw new Exception(Constants.SOMETHING_WENT_WRONG);
        }
        setShopOutput(barberIntf.getAllShopsByArea(appContect, shopArea, getRankIndex()));
    }
}
