package com.kwery.tests.controllers.apis.integration.security;

public class ApiSecurityTestVo {
    private String route;
    private HttpMethod httpMethod;
    private Object params;
    private boolean isSecure;

    public ApiSecurityTestVo(String route, boolean isSecure, HttpMethod httpMethod, Object params) {
        this.route = route;
        this.isSecure = isSecure;
        this.httpMethod = httpMethod;
        this.params = params;
    }

    public ApiSecurityTestVo(String route, boolean isSecure, HttpMethod httpMethod) {
        this.route = route;
        this.isSecure = isSecure;
        this.httpMethod = httpMethod;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public void setSecure(boolean secure) {
        isSecure = secure;
    }

    public static enum HttpMethod {
        GET, POST
    }
}
