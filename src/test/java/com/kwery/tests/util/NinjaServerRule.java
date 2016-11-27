package com.kwery.tests.util;

import com.google.inject.Injector;
import ninja.utils.NinjaTestServer;
import org.junit.rules.ExternalResource;

public class NinjaServerRule extends ExternalResource {
    public NinjaTestServer ninjaTestServer;

    @Override
    public void before() {
        ninjaTestServer = new NinjaTestServer();
    }

    @Override
    public void after() {
        ninjaTestServer.shutdown();
    }

    public String getServerUrl() {
        return ninjaTestServer.getServerUrl();
    }

    public Injector getInjector() {
        return ninjaTestServer.getInjector();
    }
}
