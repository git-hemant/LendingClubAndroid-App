package com.ravneetg.lcautomatique.data;

import com.ravneetg.lcautomatique.db.models.StrategyConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class for all the strategies configuration.
 *
 * Created by HemantSingh on 1/29/2015.
 */
public class StrategiesConfig {

    private List<StrategyConfig> strategies;

    public StrategiesConfig() {
        strategies = new ArrayList<>();
    }

    public void addStrategy(StrategyConfig strategyConfig) {
        strategies.add(strategyConfig);
    }

    public List<StrategyConfig> activeStrategies() {
        List<StrategyConfig> activeStrategies = new ArrayList<>();
        for (int i = 0, size = strategies.size(); i < size; i++) {
            if (strategies.get(i).isActive()) {
                activeStrategies.add(strategies.get(i));
            }
        }
        return activeStrategies;
    }
}
