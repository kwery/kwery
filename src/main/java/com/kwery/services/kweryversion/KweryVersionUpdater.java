package com.kwery.services.kweryversion;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.KweryVersionDao;
import com.kwery.models.KweryVersionModel;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kwery.utils.ServiceStartUpOrderConstant.KWERY_VERSION_ORDER;

@Singleton
public class KweryVersionUpdater {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String RELEASE_SNAPSHOT_1_5_1 = "release-snapshot-1.5.1";
    public static final String CURRENT_VERSION = "release-snapshot-1.5.1";

    protected KweryVersionDao kweryVersionDao;

    @Inject
    public KweryVersionUpdater(KweryVersionDao kweryVersionDao) {
        this.kweryVersionDao = kweryVersionDao;
    }

    @Start(order = KWERY_VERSION_ORDER)
    public void update() {
        KweryVersionModel model = kweryVersionDao.get();

        if (model == null) {
            model = new KweryVersionModel();
            logger.info("Updating kwery version to {}", CURRENT_VERSION);
        } else {
            logger.info("Updating kwery version from {} to {}", model.getVersion(), CURRENT_VERSION);
        }

        model.setVersion(CURRENT_VERSION);
        kweryVersionDao.save(model);
    }
}
