package com.ravneetg.lcautomatique.request.internal;

import com.ravneetg.lcautomatique.request.data.response.NotesOwnedResponseData;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by HemantSingh on 1/19/2015.
 */
public class NotesOwnedRequest extends AbstractRequest {

    public NotesOwnedRequest(char[] investorId, char[] apiKey) {
        super(investorId, apiKey);
    }

    public NotesOwnedResponseData notesOwned() {
        HttpRequestBase request = prepareRequest("notes", Type.ACCOUNT);
        RawResponseWrapper wrapper = executeRequest(request, false);
        return new NotesOwnedResponseData(wrapper.getInputStream(), wrapper.getException(), wrapper.getStatusCode());
    }
}
