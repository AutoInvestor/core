package io.autoinvestor.infrastructure.fetchers;

public class PriceNotAvailableException extends RuntimeException {
    public PriceNotAvailableException(String message) {
        super(message);
    }
}
