package com.jofi.jofi.model;

public class StockMetric {
    private String symbol;
    private double high;
    private double low;
    private double percentReturn;
    private double first;
    private double last;

    public StockMetric(String symbol, double high, double low, double first, double last, double average) {
        this.symbol = symbol;
        this.high = high;
        this.low = low;
        this.first = first;
        this.last = last;
        this.percentReturn = average;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getFirst() {
        return first;
    }
    public double getLast() {
        return last;
    }
    
    public double getPercentReturn() {
        return percentReturn;
    }
}
