package CurrencyBot;

public interface CurrencyConvector {
    static CurrencyConvector getInstance(){
        return new CurrencyConvectorImplement();
    }
    double getConversionRate(Currency original, Currency target);
}
