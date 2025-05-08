package io.autoinvestor.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

public class Asset extends AggregateRoot {
    private AssetId id;
    private Mic mic;
    private Ticker ticker;
    private Date createdAt;
    private Date updatedAt;

    private Asset(Mic mic, Ticker ticker) {
        this.id = AssetId.generate();
        this.mic = mic;
        this.ticker = ticker;
        this.createdAt = new Date();
        this.updatedAt = new Date();

        this.recordEvent(AssetWasRegisteredEvent.from(this));
    }

    public static Asset create(String mic, String ticker) {
        return new Asset(Mic.from(mic), Ticker.from(ticker));
    }

    public String mic() {
        return mic.value();
    }

    public String ticker() {
        return ticker.value();
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
