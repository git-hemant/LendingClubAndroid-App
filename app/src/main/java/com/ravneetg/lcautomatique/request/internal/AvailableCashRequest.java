package com.ravneetg.lcautomatique.request.internal;

import com.ravneetg.lcautomatique.request.data.response.AvailableCashResponseData;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by HemantSingh on 1/18/2015.
 */
public class AvailableCashRequest extends AbstractRequest {

    public AvailableCashRequest(char[] investorId, char[] apiKey) {
        super(investorId, apiKey);
    }

    public AvailableCashResponseData availableCash() {
        HttpRequestBase request = prepareRequest("availableCash", Type.ACCOUNT);
        RawResponseWrapper wrapper = executeRequest(request, true);
        return new AvailableCashResponseData(wrapper.getJsonObject(), wrapper.getException(), wrapper.getStatusCode());
    }
}