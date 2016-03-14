package com.ravneetg.lcautomatique.request.data.response;

import org.json.JSONObject;

/**
 * Created by HemantSingh on 1/18/2015.
 */
public class AvailableCashResponseData extends ResponseData {

    public static final String AVAILABLE_CASH = AccountSummaryResponseData.AVAILABLE_CASH;

   public AvailableCashResponseData(JSONObject response, Exception exception, int responseCode) {
        super(response, exception, responseCode);
    }
}
