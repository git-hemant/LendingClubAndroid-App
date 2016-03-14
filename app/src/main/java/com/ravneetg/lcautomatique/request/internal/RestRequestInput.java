package com.ravneetg.lcautomatique.request.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ravneetg.lcautomatique.request.data.request.SubmitOrdersRequestData;

/**
 * Created by HemantSingh on 1/18/2015.
 */
public class RestRequestInput {
    private String outputClass;
    private char[] investorId;
    private char[] apiKey;

    // TODO - Maybe we can refactor the class structure so that following fields are not present here.
    // This flag is used only by the ListedLoans request.
    private boolean showAll;
    // This field is used only by the SubmittedOrdersRequest
    private SubmitOrdersRequestData submitOrdersRequestData;

    public RestRequestInput(char[] investorId, char[] apiKey, String outputClass) {
        this.investorId = investorId;
        this.apiKey = apiKey;
        this.outputClass = outputClass;
    }

    public SubmitOrdersRequestData getSubmitOrdersRequestData() {
        return submitOrdersRequestData;
    }

    public void setSubmitOrdersRequestData(SubmitOrdersRequestData submitOrdersRequestData) {
        this.submitOrdersRequestData = submitOrdersRequestData;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public String getOutputClass() {
        return outputClass;
    }

    public char[] getInvestorId() {
        return investorId;
    }

    public char[] getApiKey() {
        return apiKey;
    }
}