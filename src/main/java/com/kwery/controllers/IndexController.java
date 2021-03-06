package com.kwery.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;

import static com.google.common.base.Optional.of;
import static com.kwery.controllers.apis.UserApiController.SESSION_USERNAME_KEY;

@Singleton
public class IndexController {
    @Inject
    private Messages messages;

    public Result index(Context context) throws JsonProcessingException {
        Result html = Results.html();
        String title = messages.get(MessageKeys.TITLE, context, of(html)).get();
        html.render("title", title);
        html.render("userAuthenticated", String.valueOf(isUserAuthenticated(context)));
        return html;
    }

    private boolean isUserAuthenticated(Context context) {
        return  !"".equals(Strings.nullToEmpty(context.getSession().get(SESSION_USERNAME_KEY)));
    }
}
