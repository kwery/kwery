package com.kwery.tests.fluentlenium.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a class extending FluentPage is not behind authentication.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Unauthenticated {
}
