package com.ravneetg.lcautomatique.request.data.response;

import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by HemantSingh on 1/18/2015.
 */
public abstract class ResponseData extends ResponseObject {

    public static final int RESPONSE_SUCCESS = 200;
    private int responseCode;
    private Exception exception;

    public ResponseData(JSONObject response, Exception exception, int responseCode) {
        super(response);
        this.exception = exception;
        this.responseCode = responseCode;
    }

    public ResponseData(InputStream inputStream, Exception exception, int responseCode) {
        super(inputStream);
        this.exception = exception;
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Exception getException() {
        return exception;
    }
}
