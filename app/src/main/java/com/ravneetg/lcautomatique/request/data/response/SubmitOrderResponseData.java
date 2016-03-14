package com.ravneetg.lcautomatique.request.data.response;

import org.json.JSONObject;

/**
 * Created by HemantSingh on 1/20/2015.
 */
public class SubmitOrderResponseData extends ResponseObject {
    public static final String LOAN_ID = "loanId";
    public static final String REQUESTED_AMT = "requestedAmount";
    public static final String INVESTED_AMT = "investedAmount";
    public static final String EXECUTION_STATUSS = "executionStatus";

    public SubmitOrderResponseData(JSONObject responseData) {
        super(responseData);
    }
}
