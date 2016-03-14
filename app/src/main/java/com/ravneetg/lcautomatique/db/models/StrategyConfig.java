package com.ravneetg.lcautomatique.db.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HemantSingh on 1/26/2015.
 */
public class StrategyConfig {
    public static final String DERIVED_FILTER_MIN_PERCENTAGE_FUNDED = "minPercentageFunded";
    public static final String DERIVED_FILTER_MIN_POPULARITY_INDEX = "minPopularityIndex";
    private String strategyName;
    //TODO - This should be number
    private String targetPortfolio;
    private boolean active;
    private int maxLoansPerDay;
    private int amountPerNote;
    // This filter will include both data filter and derived data
    // filter like funded percentage, popularity, etc.
    private JSONObject filter;

    public JSONObject getFilter() {
        return filter;
    }

    public void setFilter(JSONObject filter) {
        this.filter = filter;
    }

    public String getTargetPortfolio() {
        return targetPortfolio;
    }

    public void setTargetPortfolio(String targetPortfolio) {
        this.targetPortfolio = targetPortfolio;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getMaxLoansPerDay() {
        return maxLoansPerDay;
    }

    public void setMaxLoansPerDay(int maxLoansPerDay) {
        this.maxLoansPerDay = maxLoansPerDay;
    }

    public int getAmountPerNote() {
        return amountPerNote;
    }

    public void setAmountPerNote(int amountPerNote) {
        this.amountPerNote = amountPerNote;
    }

    public int getMinPercentageFunded() throws JSONException {
        if (getFilter() != null && getFilter().has(DERIVED_FILTER_MIN_PERCENTAGE_FUNDED)) {
            return getFilter().getInt(DERIVED_FILTER_MIN_PERCENTAGE_FUNDED);
        }
        return 0;
    }

    public double getMinPopularityIndex() throws JSONException {
        if (getFilter() != null && getFilter().has(DERIVED_FILTER_MIN_POPULARITY_INDEX)) {
            return getFilter().getDouble(DERIVED_FILTER_MIN_POPULARITY_INDEX);
        }
        return 0;
    }
}
