package com.ravneetg.lcautomatique.request.internal;

import com.ravneetg.lcautomatique.request.InvalidRequestException;
import com.ravneetg.lcautomatique.request.data.request.SubmitOrdersRequestData;
import com.ravneetg.lcautomatique.request.data.response.SubmitOrdersResponseData;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by HemantSingh on 1/20/2015.
 */
public class SubmitOrdersRequest extends AbstractRequest {

    private SubmitOrdersRequestData requestObject;
    public SubmitOrdersRequest(char[] investorId, char[] apiKey, SubmitOrdersRequestData requestObject) {
        super(investorId, apiKey);
        this.requestObject = requestObject;
    }

    public SubmitOrdersResponseData submitOrders() {
        HttpPost request = (HttpPost) prepareRequest("orders", Type.ACCOUNT_POST);
        HttpEntity httpEntity = null;
        try {
            String requestString = requestObject.getRequestObject().toString(4);
            httpEntity = new StringEntity(requestString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InvalidRequestException(e);
        }
        catch (JSONException e) {
            throw new InvalidRequestException(e);
        }
        request.setEntity(httpEntity);
        RawResponseWrapper wrapper = executeRequest(request, true);
        JSONObject jsonObject = wrapper.getJsonObject();
        return new SubmitOrdersResponseData(wrapper.getJsonObject(), wrapper.getException(), wrapper.getStatusCode());
    }
}