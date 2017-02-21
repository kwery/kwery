package com.kwery.services.kwerydirectory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.utils.KweryDirectory;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kwery.utils.ServiceStartUpOrderConstant.KWERY_DIRECTORY_CHECKER_ORDER;

@Singleton
public class KweryDirectoryChecker {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final KweryDirectory kweryDirectory;

    @Inject
    public KweryDirectoryChecker(KweryDirectory kweryDirectory) {
        this.kweryDirectory = kweryDirectory;
    }

    @Start(order = KWERY_DIRECTORY_CHECKER_ORDER)
    public void check() {
        logger.info("Directory check - start");
        kweryDirectory.checkAndRepairDirectories();
        logger.info("Directory check - end");
    }
}
