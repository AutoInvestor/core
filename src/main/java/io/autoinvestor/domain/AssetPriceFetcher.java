package io.autoinvestor.domain;

import java.util.Date;

public interface AssetPriceFetcher {
  float priceOn(Asset asset, Date date);
}
