package io.autoinvestor.application;

import java.util.Date;

public record GetAssetPriceCommand(String assetId, Date date) { }
