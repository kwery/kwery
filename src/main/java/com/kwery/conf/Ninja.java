package com.kwery.conf;

import ninja.Context;
import ninja.NinjaDefault;
import ninja.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ninja extends NinjaDefault {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Result getBadRequestResult(Context context, Exception exception) {
        logger.error("Bad request - ", exception);
        return super.getBadRequestResult(context, exception);
    }
}
