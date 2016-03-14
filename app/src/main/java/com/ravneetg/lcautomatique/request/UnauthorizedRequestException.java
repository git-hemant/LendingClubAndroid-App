package com.ravneetg.lcautomatique.request;

/**
 * Created by HemantSingh on 1/18/2015.
 */
public class UnauthorizedRequestException extends RuntimeException {

    public UnauthorizedRequestException(String msg) {
        super(msg);
    }

    public UnauthorizedRequestException(String msg, Exception e) {
        super(msg, e);
    }
}
