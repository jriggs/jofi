package com.jofi.jofi.model;

import java.util.List;
import java.util.Map;

public class StockResponse {
    private  Map<String, List<StockData>> stockData;
    private List<StockMetric> metrics;
    
    public StockResponse(Map<String, List<StockData>> stockData, List<StockMetric> metrics) {
        this.stockData = stockData;
        this.metrics = metrics;
    }

    public Map<String, List<StockData>> getStockData() {
        return stockData;
    }

    public void setStockData(Map<String, List<StockData>> rawData) {
        this.stockData = rawData;
    }
    
    public List<StockMetric> getMetrics() {
        return metrics;
    }
    
    public void setMetrics(List<StockMetric> metrics) {
        this.metrics = metrics;
    }
}