package com.kwery.services.license;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import ninja.scheduler.Schedule;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class LicenseChecker {
    protected Logger logger = LoggerFactory.getLogger(LicenseChecker.class);

    protected final UserDao userDao;
    protected final NinjaProperties ninjaProperties;

    @Inject
    public LicenseChecker(UserDao userDao, NinjaProperties ninjaProperties) {
        this.userDao = userDao;
        this.ninjaProperties = ninjaProperties;
    }

    @Schedule(delay = 15, timeUnit = TimeUnit.MINUTES)
    public void checkLicense() {
        logger.debug("Checking license");
        if (ninjaProperties.getBoolean("trial")) {
            logger.debug("This is a trial copy");
            List<User> users = userDao.list();
            if (!users.isEmpty()) {
                long created = users.get(0).getCreated();
                if (System.currentTimeMillis() - created > TimeUnit.DAYS.toMillis(30)) {
                    logger.error("Your trial period is over, shutting down Kwery");
                    System.exit(-1);
                }
            }
        }
    }
}
