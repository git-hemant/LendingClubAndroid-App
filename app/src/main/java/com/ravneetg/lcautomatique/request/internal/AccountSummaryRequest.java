package com.ravneetg.lcautomatique.request.internal;

import com.ravneetg.lcautomatique.request.data.response.AccountSummaryResponseData;

import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONObject;

/**
 * Created by HemantSingh on 1/18/2015.
 */
public class AccountSummaryRequest extends AbstractRequest {

    public AccountSummaryRequest(char[] investorId, char[] apiKey) {
        super(investorId, apiKey);
    }

    public AccountSummaryResponseData accountSummary() {
        HttpRequestBase request = prepareRequest("summary", Type.ACCOUNT);
        RawResponseWrapper wrapper = executeRequest(request, true);
        JSONObject jsonObject = wrapper.getJsonObject();
        return new AccountSummaryResponseData(wrapper.getJsonObject(), wrapper.getException(), wrapper.getStatusCode());
    }
}
