import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StockDailyData } from '../models/stockData';
import { StockMetric } from '../models/stockMetric';

@Injectable({ providedIn: 'root' })
export class StockService {
  private baseUrl = '/api/stock';

  constructor(private http: HttpClient) {}

  getStockData(symbol: string): Observable<StockDailyData[]> {
    const url = `${this.baseUrl}/daily?symbol=${symbol}&period=1mo`;
    return this.http.get<StockDailyData[]>(url);
  }

  getStockByAggression(level: string): Observable<{
    stockData: Record<string, StockDailyData[]>;
    metrics: any[];
  }> {
    return this.http.get<{
      stockData: Record<string, StockDailyData[]>;
      metrics: StockMetric[];
    }>(`${this.baseUrl}/aggressive/${level}`);
  }
}
