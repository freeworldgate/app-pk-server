package com.union.app.common.api.registry;


public class ApiURI
{

    private static final String API_URI_PREFIX = "API";
    private static final String API_URI_SPLITE = "/";
    private static final String API_URI_SPACE = "";

    private String uri;


    public boolean equalsUrl(String uri) {

        String requestUri = uri.replace(API_URI_SPLITE,API_URI_SPACE).trim().toUpperCase();
        if(requestUri.startsWith(API_URI_PREFIX))
        {
            requestUri = requestUri.substring(3);
        }
        String myUri = this.uri.replace(API_URI_SPLITE,API_URI_SPACE).trim().toUpperCase();
        if(myUri.equals(requestUri))
        {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
