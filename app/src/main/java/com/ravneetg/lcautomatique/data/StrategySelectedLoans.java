package com.ravneetg.lcautomatique.data;

import com.ravneetg.lcautomatique.db.models.StrategyConfig;

import org.json.JSONObject;

import java.util.List;

/**
 * This class would be used to store the loans which are selected for the given strategy.
 *
 * Created by HemantSingh on 1/29/2015.
 */
public class StrategySelectedLoans {

    private StrategyConfig strategyConfig;
    private List<JSONObject> selectedLoans;

    public StrategySelectedLoans(StrategyConfig strategyConfig, List<JSONObject> selectedLoans) {
        this.strategyConfig = strategyConfig;
        this.selectedLoans = selectedLoans;
    }

    public StrategyConfig getStrategyConfig() {
        return strategyConfig;
    }

    public List<JSONObject> getSelectedLoans() {
        return selectedLoans;
    }
}
