package com.kwery.tests.util;

import com.google.common.base.Optional;
import com.google.inject.Injector;
import ninja.Bootstrap;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepoDashTestBase {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Guice Injector to get DAOs
     */
    private Injector injector;

    private NinjaMode ninjaMode;

    private Bootstrap bootstrap;

    /**
     * Constructor checks if NinjaMode was set in System properties, if not,
     * NinjaMode.test is used as default
     */
    public RepoDashTestBase() {
        Optional<NinjaMode> mode = NinjaModeHelper
                .determineModeFromSystemProperties();
        ninjaMode = mode.isPresent() ? mode.get() : NinjaMode.test;

    }

    /**
     * Constructor, receives the test mode to choose the database
     *
     * @param testMode
     */
    public RepoDashTestBase(NinjaMode testMode) {
        ninjaMode = testMode;
    }

    @Before
    public final void initialize() {
        TestUtil.clearCreatedIds();
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(ninjaMode);
        bootstrap = new Bootstrap(ninjaProperties);
        bootstrap.boot();
        this.injector = bootstrap.getInjector();
    }

    @After
    public final void stop() {
        logger.info("Shutting down RepoDashTestBase");
        bootstrap.shutdown();
    }


    /**
     * Get the DAO instances ready to use
     *
     * @param clazz
     * @return DAO
     */
    public <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}
