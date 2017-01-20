package com.kwery.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;

import static ninja.Results.contentType;

@Singleton
public class MessageApiController {
    public static final String JS =
            ";((function(){ " +
                    "define(function(){ " +
                    "return { " +
                    "messages: %s " +
                    "} " +
                    "}); " +
                    "})());";

    protected final Messages messages;

    @Inject
    public MessageApiController(Messages messages) {
        this.messages = messages;
    }

    public Result getAllMessages(Context context) throws JsonProcessingException {
        Result result = contentType("application/javascript");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(messages.getAll(context, Optional.of(result)));

        return result.renderRaw(String.format(JS, json).getBytes());
    }
}
