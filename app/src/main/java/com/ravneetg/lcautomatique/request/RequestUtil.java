package com.ravneetg.lcautomatique.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ravneetg.lcautomatique.request.data.request.SubmitOrderRequestData;
import com.ravneetg.lcautomatique.request.data.request.SubmitOrdersRequestData;
import com.ravneetg.lcautomatique.request.data.response.AccountSummaryResponseData;
import com.ravneetg.lcautomatique.request.data.response.AvailableCashResponseData;
import com.ravneetg.lcautomatique.request.data.response.NotesOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.PortfoliosOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.SubmitOrdersResponseData;
import com.ravneetg.lcautomatique.request.internal.AsyncRESTRequest;
import com.ravneetg.lcautomatique.request.internal.RestRequestInput;
import com.ravneetg.lcautomatique.utils.PreferencesUtil;

import java.util.List;

/**
 * This should be the main class which everyone should invoke to invoke any of the REST API
 * and specify the call back.
 *
 * If any new REST API needs to be added then this class should be updated in addition of
 * @code AsyncRRESTRequest.
 *
 * Created by HemantSingh on 1/18/2015.
 */
public class RequestUtil {

    private RequestUtil(){}

    // Get AccountSummary - this method should always be called in some background thread.
    public static AccountSummaryResponseData requestAccountSummary(Context ctx) {
        RestRequestInput requestInput = createRestRequestInput(ctx, AccountSummaryResponseData.class.getName());
        AsyncRESTRequest<AccountSummaryResponseData> request = new AsyncRESTRequest<>(null);
        return request.processRequest(requestInput);
    }

    // Get object of AvailableCash.
    public static AvailableCashResponseData requestAvailableCash(Context ctx) {
        RestRequestInput requestInput = createRestRequestInput(ctx, AvailableCashResponseData.class.getName());
        AsyncRESTRequest<AvailableCashResponseData> request = new AsyncRESTRequest<>(null);
        return request.processRequest(requestInput);
    }

    // Get object of ListedNotesOwned.
    public static NotesOwnedResponseData requestNotesOwned(Context ctx) {
        RestRequestInput requestInput = createRestRequestInput(ctx, NotesOwnedResponseData.class.getName());
        AsyncRESTRequest<NotesOwnedResponseData> request = new AsyncRESTRequest<>(null);
        return request.processRequest(requestInput);
    }

    // Get object of PortfoliosOwnedResponse.
    public static PortfoliosOwnedResponseData requestPortfoliosOwned(Context ctx) {
        RestRequestInput requestInput = createRestRequestInput(ctx, PortfoliosOwnedResponseData.class.getName());
        AsyncRESTRequest<PortfoliosOwnedResponseData> request = new AsyncRESTRequest<>(null);
        return request.processRequest(requestInput);
    }

    // Get object of ListedLoans.
    public static NotesOwnedResponseData listedLoans(Context ctx, boolean showAll) {
        RestRequestInput requestInput = createRestRequestInput(ctx, NotesOwnedResponseData.class.getName());
        requestInput.setShowAll(showAll);
        AsyncRESTRequest<NotesOwnedResponseData> request = new AsyncRESTRequest<>(null);
        return request.processRequest(requestInput);
    }

    // Get object of SubmitOrdersResponseData
    public static SubmitOrdersResponseData submitOrders(Context ctx, List<SubmitOrderRequestData> requestedLoans) {
        RestRequestInput requestInput = createRestRequestInput(ctx, SubmitOrdersRequestData.class.getName());
        SubmitOrdersRequestData requestData = new SubmitOrdersRequestData(PreferencesUtil.getInvestorId(ctx), requestedLoans);
        requestInput.setSubmitOrdersRequestData(requestData);
        AsyncRESTRequest<SubmitOrdersResponseData> request = new AsyncRESTRequest<>(null);
        return request.processRequest(requestInput);
    }

    private static RestRequestInput createRestRequestInput(Context ctx, String outputClass) {
        char[] investorId = PreferencesUtil.getInvestorId(ctx);
        char[] apiKey = PreferencesUtil.getAPIKey(ctx);
        return new RestRequestInput(investorId, apiKey, outputClass);
    }
}