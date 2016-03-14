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
public class PortfoliosOwnedResponseData extends ResponseData {
    public static String MY_PORTFOLIOS = "myPortfolios";
    public static final String PORTFOLIO_ID = "portfolioId";
    public static final String PORTFOLIO_NAME = "portfolioName";
    public static final String PORTFOLIO_DESC = "portfolioDescription";

    public PortfoliosOwnedResponseData(JSONObject response, Exception exception, int responseCode) {
        super(response, exception, responseCode);
    }
}
