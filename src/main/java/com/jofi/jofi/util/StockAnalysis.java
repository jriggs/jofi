package com.jofi.jofi.util;
import com.jofi.jofi.model.StockMetric;
import com.jofi.jofi.model.StockData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.DoubleSummaryStatistics;
import java.util.Comparator;

public class StockAnalysis {
   
    public static List<StockMetric> calculateMetrics(Map<String, List<StockData>> stockDataMap) {
        List<StockMetric> metrics = stockDataMap.entrySet().stream()
                .map(entry -> {
                    String symbol = entry.getKey();
                    List<StockData> data = entry.getValue();
                    
                    if (data == null || data.isEmpty()) return null;
                    
                    List<StockData> sortedData = data.stream()
                            .sorted(Comparator.comparing(StockData::getDate))
                            .collect(Collectors.toList());
                    
                    DoubleSummaryStatistics highStats = sortedData.stream()
                            .mapToDouble(StockData::getHigh)
                            .summaryStatistics();
                    
                    DoubleSummaryStatistics lowStats = sortedData.stream()
                            .mapToDouble(StockData::getLow)
                            .summaryStatistics();
                    
                    double high = highStats.getMax();
                    double low = lowStats.getMin();
                    
                    double first = sortedData.get(0).getOpen();
                    double last = sortedData.get(sortedData.size() - 1).getClose();
                    
                    // Calculate percentage return from first to last
                    double percentReturn = first != 0 ? ((last - first) / first) : 0.0;
                    
                    return new StockMetric(symbol, high, low, first, last, percentReturn);
                })
                .filter(metric -> metric != null)
                .collect(Collectors.toList());
        
        // Add ALLSYMBOLS metric that averages all other metrics
        if (!metrics.isEmpty()) {
            double avgHigh = metrics.stream()
                    .mapToDouble(StockMetric::getHigh)
                    .average()
                    .orElse(0.0);
            
            double avgLow = metrics.stream()
                    .mapToDouble(StockMetric::getLow)
                    .average()
                    .orElse(0.0);
            
            double avgFirst = metrics.stream()
                    .mapToDouble(StockMetric::getFirst)
                    .average()
                    .orElse(0.0);
            
            double avgLast = metrics.stream()
                    .mapToDouble(StockMetric::getLast)
                    .average()
                    .orElse(0.0);
            
            double avgPercentReturn = metrics.stream()
                    .mapToDouble(StockMetric::getPercentReturn)
                    .average()
                    .orElse(0.0);
            
            metrics.add(new StockMetric("ALLSYMBOLS", avgHigh, avgLow, avgFirst, avgLast, avgPercentReturn));
        }
        
        return metrics;
    }
}