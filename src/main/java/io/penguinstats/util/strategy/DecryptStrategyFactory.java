package io.penguinstats.util.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DecryptStrategyFactory {

    private Map<DecryptStrategyName, DecryptStrategy> strategies;

    @Autowired
    public DecryptStrategyFactory(Set<DecryptStrategy> strategySet) {
        createStrategy(strategySet);
    }

    public DecryptStrategy findStrategy(DecryptStrategyName strategyName) {
        return strategies.get(strategyName);
    }

    private void createStrategy(Set<DecryptStrategy> strategySet) {
        strategies = new HashMap<DecryptStrategyName, DecryptStrategy>();
        strategySet.forEach(strategy -> strategies.put(strategy.getStrategyName(), strategy));
    }

}
