package com.ravneetg.lcautomatique.request.data.response;

import org.json.JSONObject;

/**
 * Created by HemantSingh on 1/18/2015.
 */
public class AccountSummaryResponseData extends ResponseData {
    public static final String AVAILABLE_CASH = "availableCash";
    public static final String ACCRUED_INTEREST = "accruedInterest";
    public static final String OUTSTANDING_PRINCIPAL = "outstandingPrincipal";
    public static final String ACCOUNT_TOTAL = "accountTotal";
    public static final String INFUNDING_BALANCE = "infundingBalance";
    public static final String RECEIVED_INTEREST = "receivedInterest";
    public static final String RECEIVED_PRINCIPAL = "receivedPrincipal";
    public static final String RECIEVED_LATE_FEES = "receivedLateFees";
    public static final String TOTAL_NOTES = "totalNotes";
    public static final String TOTAL_PORTFOLIOS = "totalPortfolios";

    public AccountSummaryResponseData(JSONObject response, Exception exception, int responseCode) {
        super(response, exception, responseCode);
    }
}
