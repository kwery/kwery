package controllers.fluentlenium;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import ninja.NinjaFluentLeniumTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class FluentLeniumTest extends NinjaFluentLeniumTest {
    static final int TIMEOUT_SECONDS = 5;

    @Override
    public WebDriver getDefaultDriver() {
        System.setProperty("webdriver.chrome.driver", "/usr/lib/chromium-browser/chromedriver");
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.manage().timeouts().implicitlyWait(1, TimeUnit.DAYS);
        return chromeDriver;
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