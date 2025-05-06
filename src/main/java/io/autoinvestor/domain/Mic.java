package io.autoinvestor.domain;

public class Mic {
    private final String value;

    private Mic(String value) {
        this.value = value;
    }

    public static Mic from(String mic) {
        return new Mic(mic);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Mic{" +
                "mic='" + value + '\'' +
                '}';
    }
}
