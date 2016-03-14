package com.ravneetg.lcautomatique;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.ravneetg.lcautomatique.data.StrategySelectedLoans;
import com.ravneetg.lcautomatique.db.models.StrategyConfig;
import com.ravneetg.lcautomatique.request.RequestUtil;
import com.ravneetg.lcautomatique.request.data.request.SubmitOrderRequestData;
import com.ravneetg.lcautomatique.request.data.request.SubmitOrdersRequestData;
import com.ravneetg.lcautomatique.request.data.response.SubmitOrdersResponseData;
import com.ravneetg.lcautomatique.request.internal.AsyncRESTRequest;
import com.ravneetg.lcautomatique.request.internal.RestRequestInput;
import com.ravneetg.lcautomatique.request.process.LoanFilter;
import com.ravneetg.lcautomatique.utils.LoanUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HemantSingh on 2/4/2015.
 */
public class MainActivityTest extends InstrumentationTestCase {

    private static final String ACCOUNT = "<A/C>";
    private static final String API_KEY = "<KEY>";
    public MainActivityTest() {
        //super(MainActivity.class);
    }

    @MediumTest
    public void testSimple() {
        Log.d("app", "This is simple test");
        int i = 0;

        i++;
    }

    // It test filtering is working fine on pre-defined set of loans, and order is working fine.
    public void testCase1() {
        StringBuilder strStrategy = getResourceAsString("strategy1.txt");
        try {
            StrategyConfig strategyConfig = createStrategyConfig(new JSONObject(strStrategy.toString()));
            strStrategy = null;
            JSONArray loansArray = getLoansJSONObject();
            List<StrategySelectedLoans> selectedLoanses = filterStrategy(loansArray, strategyConfig);
            if (selectedLoanses.size() != 1) {
                throw new RuntimeException("Testcase1 - number of selected strategies should be 1 and not " + selectedLoanses.size());
            }
            StrategySelectedLoans selectedLoans = selectedLoanses.get(0);
            if (selectedLoans.getSelectedLoans().size() != 1) {
                throw new RuntimeException("Testcase1 - number of selected loans should be 1 and not " + selectedLoanses.size());
            }
            JSONObject selectedLoan = selectedLoans.getSelectedLoans().get(0);
            String selectedLoanId = LoanUtil.loanId(selectedLoan);
            if (!"40482137".equals(selectedLoanId)) {
                throw new RuntimeException("Testcase1 - selected loan id should be: 40482137 and not " + selectedLoanId);
            }

            SubmitOrderRequestData sampleOrder = new SubmitOrderRequestData();
            sampleOrder.setValue(SubmitOrderRequestData.LOAN_ID, selectedLoanId);
            // Request invalid amount, as amount should be multiple of $25.
            sampleOrder.setValue(SubmitOrderRequestData.REQUESTED_AMT, "20");
            List<SubmitOrderRequestData> requestedLoans = new ArrayList<>();
            requestedLoans.add(sampleOrder);

            RestRequestInput requestInput = new RestRequestInput(ACCOUNT.toCharArray(), API_KEY.toCharArray(), SubmitOrdersRequestData.class.getName());
            SubmitOrdersRequestData requestData = new SubmitOrdersRequestData(ACCOUNT.toCharArray(), requestedLoans);
            requestInput.setSubmitOrdersRequestData(requestData);
            AsyncRESTRequest<SubmitOrdersResponseData> request = new AsyncRESTRequest<>(null);
            SubmitOrdersResponseData responseData =  request.processRequest(requestInput);
            JSONObject orderResponse = responseData.getResponseData();
            JSONArray responseArray = orderResponse.getJSONArray(SubmitOrdersResponseData.ORDERS_ERROR);
            if (responseArray.length() != 1) {
                throw new RuntimeException("Testcase1 - order response should include error message for one loan instead got " + responseArray.length());
            }
            JSONObject loanResponseError = responseArray.getJSONObject(0);
            String errorMsg = loanResponseError.getString(SubmitOrdersResponseData.ORDER_ERROR_MSG);
            if (!"Requested amount must be atleast $25".equals(errorMsg)) {
                throw new RuntimeException("Testcase1 - got unexpected error message: " + errorMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // It test filtering is working fine on pre-defined set of loans
    public void testCase2() {
        StringBuilder strStrategy = getResourceAsString("strategy2.txt");
        try {
            StrategyConfig strategyConfig = createStrategyConfig(new JSONObject(strStrategy.toString()));
            strStrategy = null;
            JSONArray loansArray = getLoansJSONObject();
            List<StrategySelectedLoans> selectedLoanses = filterStrategy(loansArray, strategyConfig);
            if (selectedLoanses.size() != 1) {
                throw new RuntimeException("Testcase2 - number of selected strategies should be 1 and not " + selectedLoanses.size());
            }
            StrategySelectedLoans selectedLoans = selectedLoanses.get(0);
            if (selectedLoans.getSelectedLoans().size() != 6) {
                throw new RuntimeException("Testcase2 - number of selected loans should be 6 and not " + selectedLoanses.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<StrategySelectedLoans> filterStrategy(JSONArray loansArray, StrategyConfig strategyConfig) throws JSONException {
        LoanFilter loanFilter = new LoanFilter();
        List<StrategyConfig> strategies = new ArrayList<>();
        strategies.add(strategyConfig);
        return loanFilter.filter(strategies, loansArray);
    }

    private StringBuilder getResourceAsString(String res) {
        StringBuilder builder = new StringBuilder();
        try {

            InputStream inputStream = getInstrumentation().getContext().getAssets().open(res);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String s = null;
            while ((s = reader.readLine()) != null) {
                builder.append(s);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder;
    }

    private StrategyConfig createStrategyConfig(JSONObject filter) {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setActive(true);
        strategyConfig.setAmountPerNote(15);
        strategyConfig.setMaxLoansPerDay(1);
        strategyConfig.setStrategyName("Strategy1");
        strategyConfig.setFilter(filter);
        return strategyConfig;
    }

    private JSONArray getLoansJSONObject() throws JSONException {
        StringBuilder strLoans = getResourceAsString("loans.json");
        return new JSONObject(strLoans.toString()).getJSONArray("loans");
    }
}
