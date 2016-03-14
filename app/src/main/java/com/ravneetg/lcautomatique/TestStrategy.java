package com.ravneetg.lcautomatique;

import android.content.Context;

import com.ravneetg.lcautomatique.data.StrategySelectedLoans;
import com.ravneetg.lcautomatique.db.models.StrategyConfig;
import com.ravneetg.lcautomatique.request.RequestUtil;
import com.ravneetg.lcautomatique.request.data.request.SubmitOrderRequestData;
import com.ravneetg.lcautomatique.request.data.response.SubmitOrdersResponseData;
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
 * Created by HemantSingh on 1/31/2015.
 */
public class TestStrategy {
    private Context ctx;

    public TestStrategy(Context ctx) {
        this.ctx = ctx;
    }

    public void testStrategies() {
        testCase1();
        testCase2();
    }

    // It test filtering is working fine on pre-defined set of loans, and order is working fine.
    private void testCase1() {
        StringBuilder strStrategy = getResourceAsString("tests/case1/strategy1.txt");
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
            SubmitOrdersResponseData responseData = RequestUtil.submitOrders(ctx, requestedLoans);
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
    private void testCase2() {
        StringBuilder strStrategy = getResourceAsString("tests/case2/strategy2.txt");
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

    // It test for the order request
    private void testCase3() {

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
            InputStream inputStream = ctx.getAssets().open(res);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String s = null;
            while ((s = reader.readLine()) != null) {
                builder.append(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        StringBuilder strLoans = getResourceAsString("tests/loans.json");
        return new JSONObject(strLoans.toString()).getJSONArray("loans");
    }
}
