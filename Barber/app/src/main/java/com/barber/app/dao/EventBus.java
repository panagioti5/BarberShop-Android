package com.barber.app.dao;

public class EventBus extends com.google.common.eventbus.EventBus {
    private static final EventBus bus  = new EventBus();
    public static EventBus getInstance() { return bus; }
    private EventBus() {}
}