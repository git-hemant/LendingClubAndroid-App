package com.ravneetg.lcautomatique.request.process;

import com.ravneetg.lcautomatique.db.models.StrategyConfig;
import com.ravneetg.lcautomatique.utils.LoanUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by HemantSingh on 1/29/2015.
 */
class LoanFilterByStrategy {
    private StrategyConfig config;
    // The map of the loan which we need to exclude, here key would loan id and value is some boolean
    // object which is no-op. This is required because we should excluded loans if it is already selected
    // by some other strategy.
    private Map<String, Boolean> excludedLoans;

    public LoanFilterByStrategy(StrategyConfig strategyConfig, Map<String, Boolean> excludedLoans) {
        this.config = strategyConfig;
        this.excludedLoans = excludedLoans;
    }

    public List<JSONObject> filter(JSONArray loans) throws JSONException{
        List<JSONObject> filteredLoans = new ArrayList<JSONObject>();
        for (int i = 0, length = loans.length(); i < length; i++) {
            JSONObject loan = loans.getJSONObject(i);
            String loanId = LoanUtil.loanId(loan);
            // Skip following steps if this loan needs to be excluded.
            if (excludedLoans.containsKey(loanId)) continue;
            if (isLoanIncludedByData(loan)
                    && isLoanIncludedByDerivedData(loan)) {
                filteredLoans.add(loan);
            }
        }
        return filteredLoans;
    }

    // Filter by data - start
    private boolean isLoanIncludedByData(JSONObject loan) throws JSONException {
        JSONObject strategyFilter = config.getFilter();
        if (strategyFilter == null || strategyFilter.length() == 0) return true;
        for (Iterator<String> iterator = strategyFilter.keys(); iterator.hasNext();) {
            String filterField = (String) iterator.next();
            Object obj = loan.get(filterField);
            String value = obj != null ? obj.toString() : null;
            // fieldMap is the set of filters which are specified for the given field.
            JSONObject fieldMap = (JSONObject) strategyFilter.get(filterField);
            if (!isLoanIncludedByField(loan, filterField, value, fieldMap)) {
                return false;
            }
        }
        return true;
    }

    private boolean isLoanIncludedByField(JSONObject loan, String fieldName, String value, JSONObject fieldMap) throws JSONException{
        // Look for any inclusions
        if (!fieldMap.isNull("include")) {
            JSONArray fieldIncludeMap = (JSONArray) fieldMap.get("include");
            // HS - From here.
            if (fieldIncludeMap != null && !isFieldValueInMap(fieldIncludeMap, value)) {
                return false;
            }
        }

        // Look for any exclusions
        if (!fieldMap.isNull("exclude")) {
            JSONArray fieldExcludeMap = (JSONArray) fieldMap.get("exclude");
            if (fieldExcludeMap != null && isFieldValueInMap(fieldExcludeMap, value)) {
                return false;
            }
        }

        // Look for minimum length check on the value.
        if (!fieldMap.isNull("minLength")) {
            Object minLength = fieldMap.get("minLength");
            if (minLength != null) {
                // Check if the value meet the minimum length filter.
                if (isNull(value) || value.toString().length() < Integer.parseInt(minLength.toString())) {
                    return false;
                }
            }
        }
        // Look for greater than operator on the value.
        if (!fieldMap.isNull("greaterThan")) {
            Object greaterThan = fieldMap.get("greaterThan");
            if (greaterThan != null) {
                if (isNull(value)) {
                    return false;
                }
                // Check if the field value is greater than value specified in the filter.
                if (!(Double.parseDouble(value.toString()) > Double.parseDouble(greaterThan.toString()))) {
                    return false;
                }
            }
        }

        // Look for less than operator on the value.
        if (!fieldMap.isNull("lessThan")) {
            Object lessThan = fieldMap.get("lessThan");
            if (lessThan != null) {
                if (isNull(value)) {
                    return false;
                }
                // Check if the field value is less than value specified in the filter.
                if (!(Double.parseDouble(value.toString()) < Double.parseDouble(lessThan.toString()))) {
                    return false;
                }
            }
        }

        if (!fieldMap.isNull("isEmpty")) {
            Object isEmpty = fieldMap.get("isEmpty");
            if (isEmpty != null) {
                boolean shouldBeEmpty = Boolean.parseBoolean(isEmpty.toString());
                if ((shouldBeEmpty && value != null) || (!shouldBeEmpty && isNull(value))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns true if field value matches any of the values in field map.
     * @param arrayList
     * @param value
     * @return
     */
    private boolean isFieldValueInMap(JSONArray arrayList, Object value) throws JSONException {
        for (int i = 0, length = arrayList.length(); i < length; i++) {
            if (isValueSame(value, arrayList.get(i))) return true;
        }
        return false;
    }
    // Filter by data - end

    // Filter by derived data - start
    private boolean isLoanIncludedByDerivedData(JSONObject loan) throws JSONException {
        // Exclude loans which are not funded at all.
        double expectedFunded = config.getMinPercentageFunded();
        // Calculate the funded percentage only if the user expect us to do filtering on it.
        if (expectedFunded > 0) {
            double percentageFunded = LoanUtil.getPercentageFunded(loan);
            if (percentageFunded < expectedFunded) {
                return false;
            }
        }

        // Check for the loan by popularity index.
        // TODO - This function is not working.
        double dExpectedPopularityIndex = config.getMinPopularityIndex();
        if (dExpectedPopularityIndex > 0) {
            BigDecimal expectedPopIndex = new BigDecimal(dExpectedPopularityIndex);
            BigDecimal popIndex = LoanUtil.getPopularityIndex(loan);
            if (popIndex.compareTo(expectedPopIndex) != 1) {
                return false;
            }
        }
        return true;
    }
    // Filter by derived data - end
    private static boolean isValueSame(Object o1, Object o2) {
        if (o1 == o2 || o1.equals(o2)) return true;
        if (o1 != null && o2 != null && o1.toString().equals(o2.toString())) return true;
        return false;
    }

    protected boolean isNull(String v) {
        return v == null || v.length() == 0 || "null".equals(v);
    }
}
