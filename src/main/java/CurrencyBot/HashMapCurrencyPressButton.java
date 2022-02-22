package CurrencyBot;

import java.util.HashMap;
import java.util.Map;

public class HashMapCurrencyPressButton implements CurrencyInfoPressButton{
    private  final Map<Long, Currency> originalCurrency = new HashMap<>();
    private  final Map<Long, Currency> targetCurrency = new HashMap<>();

    public HashMapCurrencyPressButton() {
    }

    @Override
    public Currency getOriginalCurrency(long chadId) {
        return originalCurrency.getOrDefault(chadId, Currency.DOLLAR);
    }

    @Override
    public Currency getTargetCurrency(long chadId) {
        return targetCurrency.getOrDefault(chadId, Currency.DOLLAR);
    }

    @Override
    public void setTargetCurrency(long chadId, Currency currency) {
        targetCurrency.put(chadId, currency);
    }

    @Override
    public void setOriginalCurrency(long chadId, Currency currency) {
        originalCurrency.put(chadId, currency);
    }
}

