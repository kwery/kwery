package controllers;

import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.validation.FieldViolation;
import ninja.validation.Validation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.of;

public class ControllerUtil {
    public static Map<String, List<String>> fieldMessages(Validation validation, Context context, Messages messages, Result result) {
        Map<String, List<String>> fieldMessages = new HashMap<>();

        List<FieldViolation> beanViolations = validation.getBeanViolations();

        for (FieldViolation fieldViolation : beanViolations) {
            String field = fieldViolation.field;
            String message = messages.get(fieldViolation.constraintViolation.getMessageKey(), context, of(result)).get();

            if (!fieldMessages.containsKey(field)) {
                fieldMessages.put(field, new LinkedList<>());
            }

            fieldMessages.get(field).add(message);
        }

        return fieldMessages;
    }
}
