package io.autoinvestor.domain;

public class CompanyName {
    private final String value;

    private CompanyName(String value) {
        this.value = value;
    }

    public static CompanyName from(String value) {
        return new CompanyName(value);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
