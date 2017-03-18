package com.kwery.tests.util;

import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import ninja.conf.NinjaBaseModule;
import ninja.jpa.JpaInitializer;
import ninja.jpa.JpaModule;
import ninja.migrations.MigrationEngine;
import ninja.migrations.MigrationEngineProvider;
import ninja.migrations.MigrationInitializer;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import org.junit.After;
import org.junit.Before;

public class RepoDashDaoTestBase {
    /**
     * Persistence Service initializer
     */
    private JpaInitializer jpaInitializer;
    private MigrationInitializer migrationInitializer;
    /**
     * Guice Injector to get DAOs
     */
    private Injector injector;

    private NinjaMode ninjaMode;

    /**
     * Constructor checks if NinjaMode was set in System properties, if not,
     * NinjaMode.test is used as default
     */
    public RepoDashDaoTestBase() {
        Optional<NinjaMode> mode = NinjaModeHelper
                .determineModeFromSystemProperties();
        ninjaMode = mode.isPresent() ? mode.get() : NinjaMode.test;

    }

    /**
     * Constructor, receives the test mode to choose the database
     *
     * @param testMode
     */
    public RepoDashDaoTestBase(NinjaMode testMode) {
        ninjaMode = testMode;
    }

    @Before
    public final void initialize() {
        TestUtil.clearCreatedIds();

        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(ninjaMode);
        injector = Guice.createInjector(new NinjaBaseModule(ninjaProperties), new JpaModule(ninjaProperties), new MigrationModule());

        migrationInitializer = injector.getInstance(MigrationInitializer.class);
        migrationInitializer.start();

        jpaInitializer = injector.getInstance(JpaInitializer.class);
        jpaInitializer.start();
    }

    @After
    public final void stop() {
        jpaInitializer.stop();
    }

    /**
     * Get the DAO instances ready to use
     *
     * @param clazz
     * @return DAO
     */
    protected <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    private static class MigrationModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(MigrationEngine.class).toProvider(MigrationEngineProvider.class);
            bind(MigrationInitializer.class).asEagerSingleton();
        }
    }
}
