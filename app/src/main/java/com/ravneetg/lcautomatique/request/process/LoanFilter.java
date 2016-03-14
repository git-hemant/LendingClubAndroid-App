package com.ravneetg.lcautomatique.request.process;

import com.ravneetg.lcautomatique.data.StrategySelectedLoans;
import com.ravneetg.lcautomatique.db.models.StrategyConfig;
import com.ravneetg.lcautomatique.utils.LoanUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for filtering provided loans based on given strategies.
 *
 * Created by HemantSingh on 1/29/2015.
 */
public class LoanFilter {

    public List<StrategySelectedLoans> filter(List<StrategyConfig> strategies, JSONArray loans) throws JSONException {
        // Map which contains loans which needs to be excluded as they are selected
        Map<String, Boolean> excludeLoans = new HashMap<>();
        List<StrategySelectedLoans> strategySelectedLoans = new ArrayList<>();
        for (int i = 0; i < strategies.size(); i++) {
            StrategyConfig strategy = strategies.get(i);
            if (!isStrategyActive(strategy)) continue;

            // First apply the data filter.
            LoanFilterByStrategy filterByStrategy = new LoanFilterByStrategy(strategy, excludeLoans);
            List<JSONObject> selectedLoans = filterByStrategy.filter(loans);

            if (selectedLoans.size() > 0) {
                // First update the excluded loans, so that no future strategy can select these loans.
                for (JSONObject selectedLoan : selectedLoans) {
                    excludeLoans.put(LoanUtil.loanId(selectedLoan), Boolean.TRUE);
                }
                strategySelectedLoans.add(new StrategySelectedLoans(strategy, selectedLoans));
            }
        }
        return strategySelectedLoans;
    }

    private boolean isStrategyActive(StrategyConfig strategyConfig) {
        // TODO - If the strategy has already ordered enough loans for the day
        // then it should be deactivated for that day.
        return strategyConfig.isActive();
    }
}
