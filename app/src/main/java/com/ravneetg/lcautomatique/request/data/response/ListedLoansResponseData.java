package com.ravneetg.lcautomatique.request.data.response;

import com.ravneetg.lcautomatique.request.InvalidResponseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HemantSingh on 1/19/2015.
 */
public class ListedLoansResponseData extends ResponseData {

    // We are not listing all the fields in the loan object
    // as most of the fields we will be referring dynamically
    // from the strategy filters.
    public static final String LOAN_ID = "id";
    public static final String LOAN_AMT = "loanAmount";
    public static final String FUNDED_AMT = "fundedAmount";
    public static final String GRADE = "grade";
    public static final String SUB_GRADE = "subGrade";
    public static final String AS_OF_DATE = "asOfDate";
    public static final String LOANS = "loans";

    public ListedLoansResponseData(JSONObject response, Exception exception, int responseCode) {
        super(response, exception, responseCode);
    }
}
