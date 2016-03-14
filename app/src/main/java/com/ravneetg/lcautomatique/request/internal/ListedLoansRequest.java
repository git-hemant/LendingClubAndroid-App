package com.ravneetg.lcautomatique.request.internal;

import com.ravneetg.lcautomatique.request.data.response.ListedLoansResponseData;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by HemantSingh on 1/19/2015.
 */
public class ListedLoansRequest extends AbstractRequest {

    private boolean showAll;
    public ListedLoansRequest(char[] investorId, char[] apiKey, boolean showAll) {
        super(investorId, apiKey);
        this.showAll = showAll;
    }

    public ListedLoansResponseData listedLoans() {
        HttpRequestBase request = prepareRequest("listing?showAll=" + showAll, Type.LOANS);
        RawResponseWrapper wrapper = executeRequest(request, true);
        return new ListedLoansResponseData(wrapper.getJsonObject(), wrapper.getException(), wrapper.getStatusCode());
    }
}
