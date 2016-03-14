package com.ravneetg.lcautomatique.request.data.request;

import com.ravneetg.lcautomatique.request.InvalidRequestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by HemantSingh on 1/19/2015.
 */
public class SubmitOrdersRequestData {

    private static final String INVESTOR_ID = "aid";
    private static final String ORDERS = "orders";
    private JSONObject requestObject;
    public SubmitOrdersRequestData(char[] investorId, List<SubmitOrderRequestData> submitOrderRequestDatas) {
        requestObject = new JSONObject();
        long aid = Long.parseLong(new String(investorId));
        try {
            requestObject.put(INVESTOR_ID, aid);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0, size = submitOrderRequestDatas.size(); i < size; i++) {
                jsonArray.put(submitOrderRequestDatas.get(i).getOrderObject());
            }
            requestObject.put(ORDERS, jsonArray);
        } catch (JSONException e) {
            throw new InvalidRequestException(e);
        }
    }

    public JSONObject getRequestObject() {
        return requestObject;
    }
}