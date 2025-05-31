package io.autoinvestor.domain;

public class Ticker {
    private final String value;

    private Ticker(String value) {
        this.value = value;
    }

    public static Ticker from(String ticker) {
        return new Ticker(ticker);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Ticker{" + "ticker='" + value + '\'' + '}';
    }
}
