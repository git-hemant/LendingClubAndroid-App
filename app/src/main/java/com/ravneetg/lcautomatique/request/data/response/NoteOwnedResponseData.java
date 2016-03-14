package com.ravneetg.lcautomatique.request.data.response;

import org.json.JSONObject;

/**
 * Created by Hemant on 6/14/2015.
 */
public class NoteOwnedResponseData extends ResponseData {
    public static final String LOAN_STATUS = "loanStatus";
    public static final String LOAN_ID = "loanId";
    public static final String NOTE_ID = "noteId";
    public static final String GRADE = "grade";
    public static final String SUB_GRADE = "subGrade";

    public static final String LOAN_AMOUNT = "loanAmount";
    public static final String NOTE_AMOUNT = "noteAmount";
    public static final String INTEREST_RATE = "interestRate";
    public static final String ORDER_ID = "orderId";
    public static final String LOAN_LENGTH = "loanLength";
    public static final String ISSUE_DATE = "issueDate";
    public static final String ORDER_DATE = "orderDate";
    // LOAN_STATUS_DATE is optional field.
    public static final String LOAN_STATUS_DATE = "loanStatusDate";
    public static final String PAYMENTS_RECEIVED = "paymentsReceived";
    public static final String FUNDED_AMOUNT = "fundedAmount";

    public NoteOwnedResponseData(JSONObject response, Exception exception, int responseCode) {
        super(response, exception, responseCode);
    }
}
