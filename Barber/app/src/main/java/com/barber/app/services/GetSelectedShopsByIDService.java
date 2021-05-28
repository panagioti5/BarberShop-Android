package com.barber.app.services;

import android.content.Context;

import com.barber.app.constants.Constants;
import com.barber.app.dao.ShopDAO;
import com.barber.app.entities.Shop;
import com.barber.app.factory.Factory;

import java.util.List;

public class GetSelectedShopsByIDService  extends Service  {



    protected ShopDAO shopIntf = null;
    protected Shop shopOutput;
    protected long shopID;
    protected Context appContect;

    public GetSelectedShopsByIDService(Context context, long shopID) {
        appContect = context;
        shopIntf = new Factory().getShopByIDFactory();
    }

    @Override
    protected void validateInput() throws Exception {

    }

    @Override
    public void execute() throws Exception {
        this.validateInput();
        if (shopIntf == null) {
            throw new Exception(Constants.SOMETHING_WENT_WRONG);
        }
        setShopOutput(shopIntf.getSelectedShopByID(appContect, shopID));
    }

    public void setShopOutput(Shop shopOutput) {
        this.shopOutput = shopOutput;
    }

    public Shop getShopOutput() {
        return shopOutput;
    }

    @Override
    public List<? extends Object> getRESTOutput() {
        return null;
    }

    @Override
    public void setRankIndex(int id) {

    }
    public long getShopID() {
        return shopID;
    }

    public void setShopID(long shopID) {
        this.shopID = shopID;
    }
}
