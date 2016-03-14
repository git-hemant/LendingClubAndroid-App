package com.ravneetg.lcautomatique.request.data.response;

import com.ravneetg.lcautomatique.request.InvalidResponseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by HemantSingh on 1/19/2015.
 */
public class ResponseObject {

    private JSONObject response;
    private InputStream inputStream;

    public ResponseObject(JSONObject response) {
        this.response = response;
    }

    public ResponseObject(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public JSONObject getResponseData() {
        return response;
    }

    public InputStream getResponseStream() {
        return inputStream;
    }

    public double getDoubleValue(String field) throws InvalidResponseException {
        double value = 0;
        try {
            Object objValue = response.get(field);
            if (objValue != null) {
                value = Double.parseDouble(objValue.toString());
            }
        } catch (JSONException e) {
            throw new InvalidResponseException(e);
        }
        return value;
    }

    public String getStringValue(String field) throws InvalidResponseException {
        String value = null;
        try {
            Object objValue = response.get(field);
            if (objValue != null) {
                value = objValue.toString();
            }
        } catch (JSONException e) {
            throw new InvalidResponseException(e);
        }
        return value;
    }

}
