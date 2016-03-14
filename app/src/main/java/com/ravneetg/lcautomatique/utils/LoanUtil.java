package com.ravneetg.lcautomatique.utils;


import com.ravneetg.lcautomatique.request.data.response.ListedLoansResponseData;
import com.ravneetg.lcautomatique.request.data.response.NoteOwnedResponseData;
import com.ravneetg.lcautomatique.request.data.response.NotesOwnedResponseData;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Set of utility method for the Note JSON object.
 *
 * Created by HemantSingh on 1/24/2015.
 */
public class LoanUtil {

    private static final int PRECISION = 10;
    private LoanUtil(){}

    public static String noteStatus(JSONObject note) throws JSONException {
        return note.getString(NoteOwnedResponseData.LOAN_STATUS);
    }

    public static String loanId(JSONObject loan) throws JSONException {
        return loan.get(ListedLoansResponseData.LOAN_ID).toString();
    }

    public static double loanAmount(JSONObject loan) throws JSONException {
        return Double.parseDouble(loan.get(NoteOwnedResponseData.LOAN_AMOUNT).toString());
    }

    public static double loanFundedAmount(JSONObject loan) throws JSONException {
        return Double.parseDouble(loan.get(NoteOwnedResponseData.FUNDED_AMOUNT).toString());
    }

    public static XMLGregorianCalendar loanListedDate(JSONObject loan) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(loan.get(NotesOwnedResponseData.LOAN_LIST_DATE).toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double getPercentageFunded(JSONObject loan) throws JSONException {
        double fundedPercentage = (LoanUtil.loanFundedAmount(loan) / LoanUtil.loanAmount(loan) * 100);
        return fundedPercentage;
    }

    // This method is failing in LoanUtil.loanListedDate
    public static BigDecimal getPopularityIndex(JSONObject loan) throws JSONException {
        double percentageFunded = getPercentageFunded(loan);
        if (percentageFunded == 0.0) return null;
        XMLGregorianCalendar time = LoanUtil.loanListedDate(loan);
        // time diff in the second
        long timeDiff = (System.currentTimeMillis() - time.toGregorianCalendar().getTimeInMillis()) / 1000;
        return new BigDecimal(percentageFunded).divide(new BigDecimal(timeDiff), PRECISION, RoundingMode.HALF_UP);
    }

}
