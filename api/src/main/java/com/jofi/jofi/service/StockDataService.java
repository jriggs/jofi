package com.jofi.jofi.service;
import com.jofi.jofi.model.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StockDataService {
    
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final ExecutorService executorService;
    
    public StockDataService() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        this.mapper = new ObjectMapper();
        this.executorService = Executors.newFixedThreadPool(10); // Configurable thread pool
    }
    
    /**
     * Fetch daily stock data for a given symbol and period
     * 
     * @param symbol Stock symbol (e.g., "AAPL", "GOOGL")
     * @param period Period to fetch ("1mo", "3mo", "6mo", "1y", "2y", "5y")
     * @return List of StockData objects
     * @throws StockDataException if fetching fails
     */
    public List<StockData> getDailyData(String symbol, String period) throws StockDataException {
        validateInputs(symbol, period);
        
        String url = String.format(
            "https://query1.finance.yahoo.com/v8/finance/chart/%s?range=%s&interval=1d",
            symbol.toUpperCase(), period
        );
        
        Request request = new Request.Builder()
            .url(url)
            .addHeader("User-Agent", "StockDataService/1.0")
            .build();
            
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new StockDataException("Failed to fetch data: HTTP " + response.code());
            }
            
            String jsonData = response.body().string();
            return parseYahooFinanceResponse(jsonData, symbol);
            
        } catch (IOException e) {
            throw new StockDataException("Network error while fetching stock data", e);
        }
    }
    
    /**
     * Fetch daily stock data for multiple symbols in parallel
     * 
     * @param symbols List of stock symbols
     * @param period Period to fetch for all symbols
     * @return Map of symbol to List of StockData objects
     * @throws StockDataException if any fetch fails
     */
    public Map<String, List<StockData>> getDailyDataParallel(List<String> symbols, String period) throws StockDataException {
        if (symbols == null || symbols.isEmpty()) {
            throw new StockDataException("Symbols list cannot be null or empty");
        }
        
        // Create CompletableFuture for each symbol
        List<CompletableFuture<Map.Entry<String, List<StockData>>>> futures = symbols.stream()
            .map(symbol -> CompletableFuture.supplyAsync(() -> {
                try {
                    List<StockData> data = getDailyData(symbol, period);
                    return Map.entry(symbol, data);
                } catch (StockDataException e) {
                    throw new RuntimeException("Failed to fetch data for symbol: " + symbol, e);
                }
            }, executorService))
            .collect(Collectors.toList());
        
        // Wait for all futures to complete and collect results
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        try {
            allFutures.get(); // Wait for all to complete
            
            Map<String, List<StockData>> results = new HashMap<>();
            for (CompletableFuture<Map.Entry<String, List<StockData>>> future : futures) {
                Map.Entry<String, List<StockData>> entry = future.get();
                results.put(entry.getKey(), entry.getValue());
            }
            
            return results;
            
        } catch (Exception e) {
            throw new StockDataException("Error during parallel data fetching", e);
        }
    }
    
    /**
     * Fetch daily stock data for the last N months
     * 
     * @param symbol Stock symbol
     * @param months Number of months (1-60)
     * @return List of StockData objects
     * @throws StockDataException if fetching fails
     */
    public List<StockData> getDailyDataForMonths(String symbol, int months) throws StockDataException {
        if (months < 1 || months > 60) {
            throw new StockDataException("Months must be between 1 and 60");
        }
        
        String period = months <= 1 ? "1mo" : 
                       months <= 3 ? "3mo" : 
                       months <= 6 ? "6mo" : 
                       months <= 12 ? "1y" : 
                       months <= 24 ? "2y" : "5y";
        
        return getDailyData(symbol, period);
    }
    
    /**
     * Get the latest stock price for a symbol
     * 
     * @param symbol Stock symbol
     * @return Latest StockData object
     * @throws StockDataException if fetching fails
     */
    public StockData getLatestPrice(String symbol) throws StockDataException {
        List<StockData> data = getDailyData(symbol, "1d");
        if (data.isEmpty()) {
            throw new StockDataException("No data available for symbol: " + symbol);
        }
        return data.get(data.size() - 1);
    }
    
   private List<StockData> parseYahooFinanceResponse(String jsonData, String symbol) throws StockDataException {
    try {
        JsonNode root = mapper.readTree(jsonData);
        JsonNode chart = root.get("chart");

        if (chart == null) {
            throw new StockDataException("Invalid symbol or API error: " + symbol);
        }

        JsonNode result = chart.get("result").get(0);
        JsonNode timestamps = result.get("timestamp");
        JsonNode indicators = result.get("indicators").get("quote").get(0);

        JsonNode opens = indicators.get("open");
        JsonNode highs = indicators.get("high");
        JsonNode lows = indicators.get("low");
        JsonNode closes = indicators.get("close");
        JsonNode volumes = indicators.get("volume");

        List<StockData> stockDataList = new ArrayList<>();
        Double previousClose = null;

        for (int i = 0; i < timestamps.size(); i++) {
            if (opens.get(i).isNull() || closes.get(i).isNull()) {
                continue;
            }

            long timestamp = timestamps.get(i).asLong();
            LocalDate date = Instant.ofEpochSecond(timestamp)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();

            double open = opens.get(i).asDouble();
            double high = highs.get(i).asDouble();
            double low = lows.get(i).asDouble();
            double close = closes.get(i).asDouble();
            long volume = volumes.get(i).asLong();

            Double percentChange = null;
            if (previousClose != null && previousClose != 0.0) {
                percentChange = ((close - previousClose) / previousClose) * 100;
            }

            StockData stockData = new StockData(
                symbol,
                date,
                open,
                high,
                low,
                close,
                volume,
                percentChange
            );

            stockDataList.add(stockData);
            previousClose = close;
        }

        return stockDataList;

    } catch (Exception e) {
        throw new StockDataException("Error parsing stock data response", e);
    }
}

    
    private void validateInputs(String symbol, String period) throws StockDataException {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new StockDataException("Symbol cannot be null or empty");
        }
        
        if (period == null || period.trim().isEmpty()) {
            throw new StockDataException("Period cannot be null or empty");
        }
        
        List<String> validPeriods = List.of("1d", "5d", "1mo", "3mo", "6mo", "1y", "2y", "5y", "10y", "ytd", "max");
        if (!validPeriods.contains(period.toLowerCase())) {
            throw new StockDataException("Invalid period. Valid periods: " + String.join(", ", validPeriods));
        }
    }
    
    /**
     * Close the HTTP client and release resources
     */
    public void close() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

// Custom exception for stock data operations
class StockDataException extends Exception {
    public StockDataException(String message) {
        super(message);
    }
    
    public StockDataException(String message, Throwable cause) {
        super(message, cause);
    }
}