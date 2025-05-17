package io.autoinvestor.domain;

import java.util.Date;

public class Asset extends AggregateRoot {
    private final AssetId id;
    private final Mic mic;
    private final Ticker ticker;
    private final CompanyName name;
    private final Date createdAt;
    private Date updatedAt;

    private Asset(Mic mic, Ticker ticker, CompanyName name) {
        this.id = AssetId.generate();
        this.mic = mic;
        this.ticker = ticker;
        this.name = name;
        this.createdAt = new Date();
        this.updatedAt = new Date();

        this.recordEvent(AssetWasRegisteredEvent.from(this));
    }

    public static Asset create(String mic, String ticker, String name) {
        return new Asset(Mic.from(mic), Ticker.from(ticker), CompanyName.from(name));
    }

    public String mic() {
        return mic.value();
    }

    public String ticker() {
        return ticker.value();
    }

    public String name() {
        return name.value();
    }

    public AssetId getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Asset that = (Asset) obj;
        return mic.equals(that.mic) && ticker.equals(that.ticker);
    }

    @Override
    public String toString() {
        return mic + ":" + ticker;
    }
}
