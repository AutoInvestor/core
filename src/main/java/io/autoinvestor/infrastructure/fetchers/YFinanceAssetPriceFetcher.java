package io.autoinvestor.infrastructure.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetPriceFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;


@Component
public class YFinanceAssetPriceFetcher implements AssetPriceFetcher {

    private static final Logger logger =
            LoggerFactory.getLogger(YFinanceAssetPriceFetcher.class);

    /** ± days around the target date that we request in one call */
    private static final int DAYS_LOOKBACK_BUFFER = 7;
    private static final long SECONDS_PER_DAY = 86_400L;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public float priceOn(Asset asset, Date date) {
        // --- 1. Build period1 / period2 (Unix seconds) -----------------------
        long targetEpoch = date.toInstant().getEpochSecond();
        long period1 = targetEpoch - DAYS_LOOKBACK_BUFFER * SECONDS_PER_DAY;
        long period2 = targetEpoch + DAYS_LOOKBACK_BUFFER * SECONDS_PER_DAY;

        // --- 2. Build URL ----------------------------------------------------
        String encoded = URLEncoder.encode(asset.ticker(), StandardCharsets.UTF_8);
        String url = String.format(
                "https://query2.finance.yahoo.com/v8/finance/chart/%s"
                        + "?period1=%d&period2=%d&interval=1d&events=history",
                encoded, period1, period2);

        try {
            // --- 3. Execute HTTP request ------------------------------------
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0") // helps avoid 429s
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new PriceFetchFailedException(
                        String.format("HTTP %d for %s (%s)",
                                response.statusCode(), asset, url));
            }

            // --- 4. Parse JSON ------------------------------------------------
            JsonNode root = mapper.readTree(response.body());
            JsonNode result = root.at("/chart/result/0");
            if (result.isMissingNode()) {
                throw new PriceNotAvailableException("Empty result for " + asset);
            }

            JsonNode timestamps = result.path("timestamp");
            JsonNode closes = result.at("/indicators/quote/0/close");

            if (!timestamps.isArray() || !closes.isArray()
                    || timestamps.size() != closes.size()) {
                throw new PriceNotAvailableException("Malformed data for " + asset);
            }

            // --- 5. Walk arrays to find the latest bar ≤ target date ---------
            float chosen = Float.NaN;
            for (int i = 0; i < timestamps.size(); i++) {
                long ts = timestamps.get(i).asLong() * 1000; // to millis
                if (ts > date.getTime()) break;              // past target

                if (!closes.get(i).isNull()) {
                    chosen = closes.get(i).floatValue();
                }
            }

            if (Float.isNaN(chosen)) {
                // Fallback to meta.regularMarketPrice if available
                JsonNode fallback = result.at("/meta/regularMarketPrice");
                if (!fallback.isMissingNode() && !fallback.isNull()) {
                    chosen = (float) fallback.asDouble();
                } else {
                    throw new PriceNotAvailableException(
                            "No bar ≤ target date for " + asset);
                }
            }
            return chosen;

        } catch (IOException | InterruptedException ex) {
            logger.error("Error fetching price for {} on {}:", asset, date, ex);
            Thread.currentThread().interrupt();
            throw new PriceFetchFailedException(
                    String.format("Unable to fetch price for %s (%s)", asset, ex));
        }
    }
}
