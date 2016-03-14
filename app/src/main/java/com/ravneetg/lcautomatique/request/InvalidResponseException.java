package com.ravneetg.lcautomatique.request;

/**
 * Created by HemantSingh on 1/18/2015.
 */
public class InvalidResponseException extends RuntimeException {

    public InvalidResponseException(String msg) {
        super(msg);
    }

    public InvalidResponseException(Exception e) {
        super(e);
    }
 }
