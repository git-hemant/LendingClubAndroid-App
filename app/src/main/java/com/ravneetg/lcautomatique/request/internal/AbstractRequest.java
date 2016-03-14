package com.ravneetg.lcautomatique.request.internal;

import android.util.Log;

import com.ravneetg.lcautomatique.MainApplication;
import com.ravneetg.lcautomatique.request.ConnectionException;
import com.ravneetg.lcautomatique.request.InvalidResponseException;
import com.ravneetg.lcautomatique.request.UnauthorizedRequestException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * All REST requests will be sub-classes of this class.
 * Created by HemantSingh on 1/17/2015.
 */
public class AbstractRequest {

    protected static final String BASE_URL = "https://api.lendingclub.com/api/investor/v1/";
    private char[] investorId;
    private char[] apiKey;

    public AbstractRequest(char[] investorId, char[] apiKey) {
        this.investorId = investorId;
        this.apiKey = apiKey;
    }

    protected String getUrl(String reqSuffix, Type type) {
        String url = BASE_URL;
        if (type == Type.ACCOUNT || type == Type.ACCOUNT_POST) {
            url += "accounts/" + new String(investorId) + "/";
        } else if (type == Type.LOANS) {
            url += "loans/";
        }
        return url + reqSuffix;
    }

    protected HttpRequestBase prepareRequest(String reqSuffix, Type type) {
        String url = getUrl(reqSuffix, type);
        HttpRequestBase request;

        if (type == Type.ACCOUNT_POST) {
            request = new HttpPost(getUrl(reqSuffix, type));
        } else {
            request = new HttpGet(getUrl(reqSuffix, type));
        }

        // Authorization header is not required for the loans request.
        if (type != Type.LOANS) {
            String apiKeyStr = new String(this.apiKey);
            request.addHeader("Authorization", apiKeyStr);
        }
        // All requests we are making would be of type json.
        request.addHeader("Content-Type", "application/json");
        return request;
    }

    protected RawResponseWrapper executeRequest(HttpRequestBase request, boolean createJson) {
        int statusCode = -1;
        Exception exception = null;
        JSONObject jsonObject = null;
        InputStream inputStream = null;
        Log.d(MainApplication.TAG, "Executing REST request using URL: " + request.getURI());
        try {
            HttpResponse response = new DefaultHttpClient().execute(request);
            StatusLine statusLine = response.getStatusLine();
            statusCode = statusLine.getStatusCode();
            if (statusCode == 401) {
                exception = new UnauthorizedRequestException(statusLine.getReasonPhrase());
            }
            if (statusCode != 200) {
                exception = new InvalidResponseException((statusLine.getReasonPhrase()));
            }

            HttpEntity httpEntity = response.getEntity();
            // Check if we need to return JSON object or return stream.
            if (createJson) {
                String responseString = EntityUtils.toString(httpEntity, "UTF-8");
                if (httpEntity != null) {
                    try {
                        httpEntity.consumeContent();
                    } catch (Exception e) {
                        // Ignore any exception on consuming content.
                    }
                    // This is not required but let's still do that to avoid
                    // exceptions like following
                    // java.lang.Throwable: Explicit termination method 'close' not called
                    try {
                        httpEntity.getContent().close();
                    } catch (Exception e) {}

                }
                jsonObject = new JSONObject(responseString);
            } else {
                inputStream = httpEntity.getContent();
            }
        } catch (JSONException e) {
            exception = new InvalidResponseException(e);
        } catch (Throwable e) {
            exception = new ConnectionException(e);
        }
        if (createJson) {
            return new RawResponseWrapper(statusCode, exception, jsonObject);
        }
        return new RawResponseWrapper(statusCode, exception, inputStream);
    }

    protected static enum Type {
        // Account and Loans requests are GET requests
        // Account_Post is post request
        // Loans request doesn't API key in the header.
        ACCOUNT, LOANS, ACCOUNT_POST
    }
}
