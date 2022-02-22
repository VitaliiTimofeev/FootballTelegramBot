package CurrencyBot;

public enum Currency {
    RUB(456), DOLLAR(431), EURO(451);

    Currency(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private final int id;
}
