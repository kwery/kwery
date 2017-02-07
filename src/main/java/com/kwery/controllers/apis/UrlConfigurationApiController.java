package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.DomainConfigurationDao;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.UrlConfiguration;
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
public class UrlConfigurationApiController {
    protected Logger logger = LoggerFactory.getLogger(UrlConfigurationApiController.class);

    protected final DomainConfigurationDao domainConfigurationDao;
    protected final OverlayedNinjaProperties ninjaProperties;

    @Inject
    public UrlConfigurationApiController(DomainConfigurationDao domainConfigurationDao, OverlayedNinjaProperties ninjaProperties) {
        this.domainConfigurationDao = domainConfigurationDao;
        this.ninjaProperties = ninjaProperties;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveDomainSetting(UrlConfiguration urlConfiguration) {
        if (logger.isTraceEnabled()) logger.trace("<");
        domainConfigurationDao.save(urlConfiguration);

        if (logger.isTraceEnabled()) logger.trace(">");

        return json().render(new ActionResult(success, ""));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result getDomainSetting(Context context) {
        List<UrlConfiguration> urlConfigurations = domainConfigurationDao.get();

        UrlConfiguration urlConfiguration = null;

        if (!urlConfigurations.isEmpty()) {
            urlConfiguration = urlConfigurations.get(0);
        }

        return json().render(urlConfiguration);
    }
}
