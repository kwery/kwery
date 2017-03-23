package com.kwery.services.license;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.InstallationDetailDao;
import com.kwery.dao.UserDao;
import com.kwery.models.InstallationDetailModel;
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
    protected final InstallationDetailDao installationDetailDao;

    @Inject
    public LicenseChecker(UserDao userDao, InstallationDetailDao installationDetailDao, NinjaProperties ninjaProperties) {
        this.userDao = userDao;
        this.installationDetailDao = installationDetailDao;
        this.ninjaProperties = ninjaProperties;
    }

    @Schedule(delay = 15, timeUnit = TimeUnit.MINUTES)
    public void checkLicense() {
        logger.debug("Checking license");
        InstallationDetailModel m = installationDetailDao.get();
        if (ninjaProperties.getBoolean("trial")) {
            logger.debug("This is a trial copy");
            if (System.currentTimeMillis() - m.getInstallationEpoch() > TimeUnit.DAYS.toMillis(30)) {
                logger.error("Your trial period is over, shutting down Kwery");
                System.exit(-1);
            }
        } else {
            logger.debug("This is a licensed copy");
            if (System.currentTimeMillis() > m.getInstallationEpoch() + TimeUnit.DAYS.toMillis(365)) {
                logger.error("Your license has expired, shutting down Kwery");
                System.exit(-1);
            }
        }
    }
}
