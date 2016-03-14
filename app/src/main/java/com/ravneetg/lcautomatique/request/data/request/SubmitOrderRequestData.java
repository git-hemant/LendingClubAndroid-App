package com.ravneetg.lcautomatique.request.data.request;

import com.ravneetg.lcautomatique.request.InvalidRequestException;
import com.ravneetg.lcautomatique.request.InvalidResponseException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HemantSingh on 1/19/2015.
 */
public class SubmitOrderRequestData {

    public static final String LOAN_ID = "loanId";
    public static final String REQUESTED_AMT = "requestedAmount";
    // Portfolio id is optional field in the order request.
    public static final String PORTFOLIO_ID = "portfolioId";
    private JSONObject orderObject = new JSONObject();

    public void setValue(String field, Object value) {
        try {
            orderObject.put(field, value);
        } catch (JSONException e) {
            throw new InvalidRequestException(e);
        }
    }

    public JSONObject getOrderObject() {
        return orderObject;
    }
}
