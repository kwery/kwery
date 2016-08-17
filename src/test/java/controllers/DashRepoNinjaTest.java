package controllers;

import ninja.NinjaTest;

public class DashRepoNinjaTest extends NinjaTest {
    public String getUrl(String path) {
        String a = getServerAddress();
        a = a.substring(0, a.length() - 1);
        return a + path;
    }
}
