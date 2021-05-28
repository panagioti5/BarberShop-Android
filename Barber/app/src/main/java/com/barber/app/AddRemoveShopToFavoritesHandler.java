package com.barber.app;

import android.app.Activity;

import com.barber.app.cache.CacheManager;
import com.barber.app.codes.OperationCode;
import com.barber.app.entities.Shop;

import java.util.ArrayList;
import java.util.List;

public class AddRemoveShopToFavoritesHandler {

    private final Activity activity;
    private Shop shop;
    List<Shop> userFavorites = new ArrayList<Shop>();


    public AddRemoveShopToFavoritesHandler(Activity activity) {
        this.activity = activity;
    }

    public void setShopID(Shop shopId) {
        this.shop = shopId;
    }

    public OperationCode handle() {
        try {
            OperationCode operationResult;
            List<Shop> alreadyFavorites = (List<Shop>) new CacheManager(activity, "FAVS").read();
            if (null == alreadyFavorites || alreadyFavorites.isEmpty()) {
                alreadyFavorites = new ArrayList<Shop>();
                alreadyFavorites.add(shop);
                operationResult = OperationCode.added;
            } else if (shopPositionInList(alreadyFavorites) != -1) {
                alreadyFavorites.remove(shopPositionInList(alreadyFavorites));
                operationResult = OperationCode.removed;
            } else {
                alreadyFavorites.add(shop);
                operationResult = OperationCode.added;
            }
            new CacheManager(activity, "FAVS").save(alreadyFavorites);
            return operationResult;
        } catch (Exception e) {
            e.printStackTrace();
            return OperationCode.noAction;
        }
    }

    private int shopPositionInList(List<Shop> alreadyFavorites) {
        for (int i = 0; i < alreadyFavorites.size(); i++) {
            if (alreadyFavorites.get(i).getShopId() == shop.getShopId()) {
                return i;
            }
        }
        return -1;
    }


    public OperationCode isExists() {
        try {
            List<Shop> alreadyFavorites = (List<Shop>) new CacheManager(activity, "FAVS").read();
            if (shopPositionInList(alreadyFavorites) != -1) {
                return OperationCode.OperationSuccess;
            } else {
                return OperationCode.OperationFailed;
            }
        } catch (Exception e) {
            return OperationCode.noAction;
        }
    }
}
