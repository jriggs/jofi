export interface StockDailyData {
  symbol: string;
  date: string; // ISO date string (e.g., "2025-06-04")
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}