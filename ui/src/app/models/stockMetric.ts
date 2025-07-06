export interface StockMetric {
  symbol: string;
  high: number;
  low: number;
  first: number;
  last: number;
  percentReturn: number;
}

export interface StockMetricsResponse {
  metrics: StockMetric[];
}