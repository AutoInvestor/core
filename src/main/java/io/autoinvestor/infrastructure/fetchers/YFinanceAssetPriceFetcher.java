package io.autoinvestor.infrastructure.fetchers;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetPriceFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(YFinanceAssetPriceFetcher.class);
    private static final int DAYS_LOOKBACK_BUFFER = 7;

    static {
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

        System.setProperty(
                "yahoofinance.baseurl.quotesquery1v7",
                "https://query1.finance.yahoo.com/v6/finance/quote"
        );

        System.setProperty(
                "yahoofinance.baseurl.histquotes",
                "https://query1.finance.yahoo.com/v7/finance/download"
        );
    }

    @Override
    public float priceOn(Asset asset, Date date) {
        Calendar target = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        target.setTime(date);

        Calendar from = (Calendar) target.clone();
        from.add(Calendar.DAY_OF_MONTH, -DAYS_LOOKBACK_BUFFER);

        Calendar to = (Calendar) target.clone();
        to.add(Calendar.DAY_OF_MONTH, DAYS_LOOKBACK_BUFFER);

        try {
            Stock stock = YahooFinance.get(asset.ticker());
            if (stock == null) {
                throw new PriceNotAvailableException("No data returned for " + asset);
            }

            List<HistoricalQuote> history = stock.getHistory(from, to, Interval.DAILY);
            if (history == null || history.isEmpty()) {
                throw new PriceNotAvailableException(
                        String.format("No historical data for %s between %s and %s", asset, from.getTime(), to.getTime())
                );
            }

            HistoricalQuote bar = history.stream()
                    .filter(h -> h.getDate() != null)
                    .filter(h -> !h.getDate().after(target))
                    .max((a, b) -> a.getDate().compareTo(b.getDate()))
                    .orElseThrow(() -> new PriceNotAvailableException(
                            String.format("No historical bar found for %s on or before %s", asset, date)
                    ));

            BigDecimal close = bar.getClose() != null ? bar.getClose() : bar.getAdjClose();
            if (close == null) {
                throw new PriceNotAvailableException(
                        String.format("Close price missing for %s on %s", asset, date)
                );
            }

            return close.floatValue();
        } catch (IOException ex) {
            logger.error("Error fetching price for {} on {}:", asset, date, ex);
            throw new PriceFetchFailedException(
                    String.format("Unable to fetch price for %s from Yahoo Finance %s", asset, ex)
            );
        }
    }
}
