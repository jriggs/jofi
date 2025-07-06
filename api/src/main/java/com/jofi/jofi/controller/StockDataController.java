// use StockDataService to fetch stock data from Yahoo Finance
// and create getDailyData method to fetch daily stock data

package com.jofi.jofi.controller;
import com.jofi.jofi.service.StockDataService;
import com.jofi.jofi.util.StockAnalysis;
import com.jofi.jofi.model.StockData;
import com.jofi.jofi.model.StockMetric;
import com.jofi.jofi.model.StockResponse;
import com.jofi.jofi.model.Ticker;
import com.jofi.jofi.repository.TickerRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "https://jofi.netlify.app", allowCredentials = "true")
public class StockDataController {
    private final StockDataService stockDataService;
    private final TickerRepository tickerRepository;

    public StockDataController(StockDataService stockDataService, TickerRepository tickerRepository) {
        this.stockDataService = stockDataService;
        this.tickerRepository = tickerRepository;
    }

    /**
     * Fetch daily stock data for a given symbol and period
     *
     * @param symbol Stock symbol (e.g., "AAPL", "GOOGL")
     * @param period Period to fetch ("1mo", "3mo", "6mo", "1y", "2y", "5y")
     * @return List of StockData objects
     */
    @GetMapping("/daily")
    public ResponseEntity<List<StockData>> getDailyData(
            @RequestParam String symbol,
            @RequestParam String period) {
        try {
            List<StockData> data = stockDataService.getDailyData(symbol, period);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @GetMapping("/multi-daily")
    public ResponseEntity<StockResponse> getMultiDailyData(
        @RequestParam List<String> symbols,
        @RequestParam(defaultValue = "1mo") String period) {
        try {
            Map<String, List<StockData>> map = stockDataService.getDailyDataParallel(symbols, period);
            List<StockMetric> metrics = StockAnalysis.calculateMetrics(map);
            var stockResponse = new StockResponse(map, metrics);
            return ResponseEntity.ok(stockResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/aggressive/{level}")
    public ResponseEntity<StockResponse> getByAggressiveLevel(
        @PathVariable String level,
        @RequestParam(defaultValue = "1mo") String period) {
        var stockTickers = tickerRepository.findByAggressiveLevel(String.valueOf(level));
        if (stockTickers == null || stockTickers.isEmpty()) {
            return ResponseEntity.ok(new StockResponse(Map.of(), List.of()));
        }
        var stockTickerSymbols = stockTickers.stream()
                .map(Ticker::getSymbol)
                .toList();
        try {
            var stockData = stockDataService.getDailyDataParallel(stockTickerSymbols, period);
            var stockResponse = new StockResponse(stockData, StockAnalysis.calculateMetrics(stockData));
            return ResponseEntity.ok(stockResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new StockResponse(Map.of(), List.of()));
        }
    }
}
