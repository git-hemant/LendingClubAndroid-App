package com.ravneetg.lcautomatique.request.data.response;

import com.ravneetg.lcautomatique.request.InvalidResponseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HemantSingh on 1/20/2015.
 */
public class SubmitOrdersResponseData extends ResponseData {
    private static final String ORDER_CONFIRMATIONS = "orderConfirmations";
    private static final String ORDER_INSTRUCTION_ID = "orderInstructId";
    public static final String ORDERS_ERROR = "errors";
    public static final String ORDER_ERROR_MSG = "message";
    private List<SubmitOrderResponseData> submitOrderResponseData;

    public SubmitOrdersResponseData(JSONObject response, Exception exception, int responseCode) {
        super(response, exception, responseCode);
        if (exception == null) {
            submitOrderResponseData = new ArrayList<>();
            try {
                JSONArray orderArray = response.getJSONArray(ORDER_CONFIRMATIONS);
                for (int i = 0, length = orderArray.length(); i < length; i++) {
                    submitOrderResponseData.add(new SubmitOrderResponseData(orderArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                throw new InvalidResponseException(e);
            }
        }
    }
}
