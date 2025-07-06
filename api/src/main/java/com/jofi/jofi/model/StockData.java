package com.jofi.jofi.model;

import java.time.LocalDate;

// Data class for stock information
public class StockData {
    private final String symbol;
    private final LocalDate date;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final long volume;
    
    public StockData(String symbol, LocalDate date, double open, double high, 
                     double low, double close, long volume) {
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
    
    // Getters
    public String getSymbol() { return symbol; }
    public LocalDate getDate() { return date; }
    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public long getVolume() { return volume; }
    
    @Override
    public String toString() {
        return String.format("%s [%s] Open: %.2f, High: %.2f, Low: %.2f, Close: %.2f, Volume: %,d",
            symbol, date, open, high, low, close, volume);
    }
}
