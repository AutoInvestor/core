package io.autoinvestor.infrastructure.fetchers;

public class PriceFetchFailedException extends RuntimeException {
  public PriceFetchFailedException(String message) {
    super(message);
  }
}
