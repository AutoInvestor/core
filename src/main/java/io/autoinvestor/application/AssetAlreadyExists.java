package io.autoinvestor.application;

import io.autoinvestor.exceptions.DuplicatedException;

public class AssetAlreadyExists extends DuplicatedException {
  public AssetAlreadyExists(String message) {
    super(message);
  }
}
