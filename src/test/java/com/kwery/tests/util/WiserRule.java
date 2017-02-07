package com.kwery.tests.util;

import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import ninja.standalone.StandaloneHelper;
import org.junit.rules.ExternalResource;
import org.subethamail.wiser.Wiser;

public class WiserRule extends ExternalResource  {
    protected Wiser wiser;
    protected int port = 0;

    @Override
    public void before() {
        port = StandaloneHelper.findAvailablePort(1000, 10000);
        wiser = new Wiser(port);
        wiser.setHostname("localhost");
        wiser.start();
    }

    @Override
    public void after() {
        wiser.stop();
    }

    public int port() {
        return port;
    }

    public SmtpConfiguration smtpConfiguration() {
        SmtpConfiguration config = new SmtpConfiguration();
        config.setPort(port());
        config.setHost("localhost");
        config.setUseLocalSetting(true);
        config.setId(DbUtil.dbId());
        return config;
    }

    public EmailConfiguration emailConfiguration() {
        EmailConfiguration config = new EmailConfiguration();
        config.setFrom("admin@getkwery.com");
        config.setReplyTo("admin@getkwery.com");
        config.setId(DbUtil.dbId());
        return config;
    }

    public Wiser wiser() {
        return wiser;
    }
}
