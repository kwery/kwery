package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.UserDao;
import com.kwery.dtos.LicenseDto;
import com.kwery.models.User;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class LicenseApiController {
    protected UserDao userDao;
    protected NinjaProperties ninjaProperties;

    @Inject
    public LicenseApiController(UserDao userDao, NinjaProperties ninjaProperties) {
        this.userDao = userDao;
        this.ninjaProperties = ninjaProperties;
    }

    public Result getLicenseDetails() {
        LicenseDto dto = new LicenseDto();

        if (ninjaProperties.getBoolean("trial")) {
            dto.setLicense(false);
            List<User> users = userDao.list();
            if (!users.isEmpty()) {
                long created = users.get(0).getCreated();
                if (System.currentTimeMillis() - created > TimeUnit.DAYS.toMillis(30)) {
                    dto.setTrialPeriod(false);
                } else {
                    dto.setTrialPeriod(true);
                }
            } else {
                //Since no users are present yet, we just send license is true so that the FE does not show any license related messages
                dto.setLicense(true);
            }
        } else {
            dto.setLicense(true);
        }

        return Results.json().render(dto);
    }
}
