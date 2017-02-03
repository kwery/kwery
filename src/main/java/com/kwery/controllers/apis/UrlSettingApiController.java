package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.DomainSettingDao;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.UrlSetting;
import com.kwery.views.ActionResult;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.utils.OverlayedNinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.kwery.views.ActionResult.Status.success;
import static ninja.Results.json;

@Singleton
public class UrlSettingApiController {
    protected Logger logger = LoggerFactory.getLogger(UrlSettingApiController.class);

    protected final DomainSettingDao domainSettingDao;
    protected final OverlayedNinjaProperties ninjaProperties;

    @Inject
    public UrlSettingApiController(DomainSettingDao domainSettingDao, OverlayedNinjaProperties ninjaProperties) {
        this.domainSettingDao = domainSettingDao;
        this.ninjaProperties = ninjaProperties;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveDomainSetting(UrlSetting urlSetting) {
        if (logger.isTraceEnabled()) logger.trace("<");
        domainSettingDao.save(urlSetting);

        if (logger.isTraceEnabled()) logger.trace(">");

        return json().render(new ActionResult(success, ""));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result getDomainSetting(Context context) {
        List<UrlSetting> urlSettings = domainSettingDao.get();

        UrlSetting urlSetting = null;

        if (!urlSettings.isEmpty()) {
            urlSetting = urlSettings.get(0);
        }

        return json().render(urlSetting);
    }
}
