package com.ravneetg.lcautomatique.request;

/**
 * Created by HemantSingh on 1/19/2015.
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String msg) {
        super(msg);
    }

    public InvalidRequestException(Exception e) {
        super(e);
    }
}