package controllers.util;

import freemarker.ext.beans.HashAdapter;
import ninja.Context;
import ninja.session.Session;

import java.util.HashMap;
import java.util.Map;

public class TestSession implements Session {
    private Map<String, String> sessionValueHolder = new HashMap<>();

    @Override
    public void init(Context context) {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Map<String, String> getData() {
        return null;
    }

    @Override
    public String getAuthenticityToken() {
        return null;
    }

    @Override
    public void save(Context context) {

    }

    @Override
    public void put(String key, String value) {
        sessionValueHolder.put(key, value);
    }

    @Override
    public String get(String key) {
        return sessionValueHolder.get(key);
    }

    @Override
    public String remove(String key) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setExpiryTime(Long expiryTimeMs) {

    }
}
