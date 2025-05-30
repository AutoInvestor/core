package io.autoinvestor.application;

public record RegisterAssetCommand(String mic, String ticker, String name) {}
