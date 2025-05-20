package io.autoinvestor.infrastructure.fetchers;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetPriceFetcher;
import org.springframework.stereotype.Component;
import yahoofinance.YahooFinance;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@Component
public class YFinanceAssetPriceFetcher implements AssetPriceFetcher {

    private static final int DAYS_LOOKBACK_BUFFER = 7;

    @Override
    public float priceOn(Asset asset, Date date) {
        Calendar target = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        target.setTime(date);

        Calendar from = (Calendar) target.clone();
        from.add(Calendar.DAY_OF_MONTH, -DAYS_LOOKBACK_BUFFER);

        Calendar to = (Calendar) target.clone();
        to.add(Calendar.DAY_OF_MONTH, DAYS_LOOKBACK_BUFFER);

        try {
            Stock stock = YahooFinance.get(asset.ticker(), from, to, Interval.DAILY);
            List<HistoricalQuote> history = stock.getHistory();

            HistoricalQuote bar = history.stream()
                    .filter(h -> h.getDate() != null)
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .filter(h -> !h.getDate().after(target))
                    .findFirst()
                    .orElseThrow(() -> new PriceNotAvailableException(
                            "No historical bar found for %s on/Before %s".formatted(asset, date)));

            BigDecimal close = bar.getClose() != null ? bar.getClose() : bar.getAdjClose();
            if (close == null) {
                throw new PriceNotAvailableException(
                        "Close price missing for %s on %s".formatted(asset, date));
            }
            return close.floatValue();
        } catch (IOException ex) {
            throw new PriceFetchFailedException(
                    "Unable to fetch price for %s from Yahoo Finance".formatted(asset));
        }
    }
}
