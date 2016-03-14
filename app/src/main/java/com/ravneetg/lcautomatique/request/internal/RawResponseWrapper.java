package com.ravneetg.lcautomatique.request.internal;

import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by HemantSingh on 1/18/2015.
 */
class RawResponseWrapper {
    private int statusCode;
    private Exception exception;
    private JSONObject jsonObject;
    private InputStream inputStream;

    RawResponseWrapper(int statusCode, Exception exception, JSONObject jsonObject) {
        this.statusCode = statusCode;
        this.exception = exception;
        this.jsonObject = jsonObject;
    }

    RawResponseWrapper(int statusCode, Exception exception, InputStream inputStream) {
        this.statusCode = statusCode;
        this.exception = exception;
        this.inputStream = inputStream;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Exception getException() {
        return exception;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
