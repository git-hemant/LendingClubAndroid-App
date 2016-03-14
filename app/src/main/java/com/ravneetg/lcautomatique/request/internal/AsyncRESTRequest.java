package com.ravneetg.lcautomatique.request.internal;

import android.os.AsyncTask;

import com.ravneetg.lcautomatique.request.AsyncRequestListener;
import com.ravneetg.lcautomatique.request.InvalidRequestException;
import com.ravneetg.lcautomatique.request.data.request.SubmitOrdersRequestData;
import com.ravneetg.lcautomatique.request.data.response.AccountSummaryResponseData;
import com.ravneetg.lcautomatique.request.data.response.AvailableCashResponseData;
import com.ravneetg.lcautomatique.request.data.response.ListedLoansResponseData;
import com.ravneetg.lcautomatique.request.data.response.NotesOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.PortfoliosOwnedResponseData;

/**
 * This is the class from which all the REST requests take place and it hides the complexity
 * of individual REST implementation.
 *
 * Created by HemantSingh on 1/18/2015.
 */
public class AsyncRESTRequest<E> extends AsyncTask<RestRequestInput, String, E>{

    private AsyncRequestListener callback;
    public AsyncRESTRequest(AsyncRequestListener callback) {
        this.callback = callback;
    }

    public E processRequest(RestRequestInput requestInput) {
        char[] investorId = requestInput.getInvestorId();
        char[] apiKey = requestInput.getApiKey();
        if (investorId == null || investorId.length == 0) {
            throw new InvalidRequestException("Please specify valid investor id.");
        }
        if (apiKey == null || apiKey.length == 0) {
            throw new InvalidRequestException("Please specify valid API Key.");
        }
        if (requestInput.getOutputClass().equals(AvailableCashResponseData.class.getName())) {
            return (E) new AvailableCashRequest(investorId, apiKey).availableCash();
        } else if (requestInput.getOutputClass().equals(AccountSummaryResponseData.class.getName())) {
            return (E) new AccountSummaryRequest(investorId, apiKey).accountSummary();
        } else if (requestInput.getOutputClass().equals(NotesOwnedResponseData.class.getName())) {
            return (E) new NotesOwnedRequest(investorId, apiKey).notesOwned();
        } else if (requestInput.getOutputClass().equals(ListedLoansResponseData.class.getName())) {
            return (E) new ListedLoansRequest(investorId, apiKey, requestInput.isShowAll()).listedLoans();
        } else if (requestInput.getOutputClass().equals(PortfoliosOwnedResponseData.class.getName())) {
            return (E) new PortfoliosOwnedRequest(investorId, apiKey).portfoliosOwned();
        } else if (requestInput.getOutputClass().equals(SubmitOrdersRequestData.class.getName())) {
            return (E) new SubmitOrdersRequest(investorId, apiKey, requestInput.getSubmitOrdersRequestData()).submitOrders();
        }
        throw new RuntimeException("Invalid output class: " + requestInput.getOutputClass());
    }

    @Override
    protected E doInBackground(RestRequestInput... params) {
        return processRequest(params[0]);
    }

    @Override
    protected void onPostExecute(E e) {
        super.onPostExecute(e);
        callback.onTaskComplete(e);
    }
}
