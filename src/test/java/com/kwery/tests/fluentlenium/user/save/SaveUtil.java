package com.kwery.tests.fluentlenium.user.save;

import com.kwery.models.User;

import java.util.HashMap;
import java.util.Map;

public class SaveUtil {
    public static Map<FormField, String> toForm(User user) {
        Map<FormField, String> map = new HashMap<>();
        map.put(FormField.firstName, user.getFirstName());
        map.put(FormField.middleName, user.getMiddleName());
        map.put(FormField.lastName, user.getLastName());
        map.put(FormField.email, user.getEmail());
        map.put(FormField.password, user.getPassword());
        map.put(FormField.confirmPassword, user.getPassword());
        return map;
    }
}
