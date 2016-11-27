package com.kwery.tests.fluentlenium;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import ninja.utils.NinjaTestServer;
import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

//Contains code for NinjaFluentLeniumTest with HTMLUnitDriver taken out
public class RepoDashFluentLeniumTest extends FluentTest {
    public NinjaTestServer ninjaTestServer;

    public WebDriver getDefaultDriver() {
        System.setProperty("webdriver.chrome.driver", "/usr/lib/chromium-browser/chromedriver");
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.manage().timeouts().implicitlyWait(1, TimeUnit.DAYS);
        return chromeDriver;
    }

    @Before
    public void startServer() {
        ninjaTestServer = new NinjaTestServer();
    }

    public String getServerAddress() {
        return ninjaTestServer.getServerUrl();
    }

    @After
    public void shutdownServer() {
        ninjaTestServer.shutdown();
    }

    public Injector getInjector() {
        return ninjaTestServer.getInjector();
    }

    public String clsSel(String className, String... htmlTags) {
        ArrayList<String> l = Lists.newArrayList(ImmutableList.of("." + className));
        Collections.addAll(l, htmlTags);
        return Joiner.on(" ").join(l);
    }

    public String idSel(String id, String... htmlTags) {
        ArrayList<String> l = Lists.newArrayList(ImmutableList.of("#" + id));
        Collections.addAll(l, htmlTags);
        return Joiner.on(" ").join(l);
    }

    public String inTxtSel(String name) {
        return String.format("input[type='text'][name='%s']", name);
    }

    public String inPwdSel(String name) {
        return String.format("input[type='password'][name='%s']", name);
    }
}
