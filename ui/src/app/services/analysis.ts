import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StockMetric } from '../models/stockMetric';
import { environment } from '../../environments/environment'

@Injectable({ providedIn: 'root' })
export class AnalysisService {
  private baseUrl = `${environment.apiUrl}/analysis`;

  constructor(private http: HttpClient) {}

    analyze(metrics: StockMetric[], aggressiveLevel: string): Observable<string> {
    const params = new HttpParams().set('aggressiveLevel', aggressiveLevel);
    const url = `${this.baseUrl}/run`; 
    return this.http.post(url, metrics, {
      params,
      responseType: 'text',
    });
  }
}
