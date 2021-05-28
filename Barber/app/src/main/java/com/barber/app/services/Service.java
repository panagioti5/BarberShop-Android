package com.barber.app.services;

import java.util.List;

public abstract class Service {
    protected abstract void validateInput() throws Exception;

    public abstract void execute() throws Exception;

    public abstract List<? extends Object> getRESTOutput();

    public abstract void setRankIndex(int id);
}
