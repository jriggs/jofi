import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Ticker } from '../models/ticker';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TickerService {
  private baseUrl = '/api/tickers';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Ticker[]> {
    return this.http.get<Ticker[]>(this.baseUrl);
  }
  //TODO RENAME
  getAggressionLevels(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/aggressive/levels/list`);
  }

  create(ticker: Ticker): Observable<Ticker> {
    return this.http.post<Ticker>(this.baseUrl, ticker);
  }

  update(id: number, ticker: Ticker): Observable<Ticker> {
    return this.http.put<Ticker>(`${this.baseUrl}/${id}`, ticker);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
