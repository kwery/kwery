package com.kwery.controllers;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;

@Singleton
public class MessageApiController {
    protected final Messages messages;

    @Inject
    public MessageApiController(Messages messages) {
        this.messages = messages;
    }

    public Result getAllMessages(Context context) {
        Result json = Results.json();
        return json.render(messages.getAll(context, Optional.of(json)));
    }
}
