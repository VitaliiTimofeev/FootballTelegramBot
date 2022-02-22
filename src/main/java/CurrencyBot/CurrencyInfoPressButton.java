package CurrencyBot;

public interface CurrencyInfoPressButton {

    static CurrencyInfoPressButton getInstance(){
        return new HashMapCurrencyPressButton();
    }

    Currency getOriginalCurrency(long chadId);
    Currency getTargetCurrency(long chadId);

    void setOriginalCurrency(long chadId, Currency currency);
    void setTargetCurrency(long chadId, Currency currency);
}
