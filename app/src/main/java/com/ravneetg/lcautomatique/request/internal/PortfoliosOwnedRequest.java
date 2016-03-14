package com.ravneetg.lcautomatique.request.internal;

import com.ravneetg.lcautomatique.request.data.response.PortfoliosOwnedResponseData;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by HemantSingh on 1/19/2015.
 */
public class PortfoliosOwnedRequest extends AbstractRequest {

    public PortfoliosOwnedRequest(char[] investorId, char[] apiKey) {
        super(investorId, apiKey);
    }

    public PortfoliosOwnedResponseData portfoliosOwned() {
        HttpRequestBase request = prepareRequest("portfolios", Type.ACCOUNT);
        RawResponseWrapper wrapper = executeRequest(request, true);
        return new PortfoliosOwnedResponseData(wrapper.getJsonObject(), wrapper.getException(), wrapper.getStatusCode());
    }
}
