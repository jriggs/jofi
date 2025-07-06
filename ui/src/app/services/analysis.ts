import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StockMetric } from '../models/stockMetric';

@Injectable({ providedIn: 'root' })
export class AnalysisService {
  private baseUrl = '/api/analysis';

  constructor(private http: HttpClient) {}

    analyze(metrics: StockMetric[], aggressiveLevel: string): Observable<string> {
    const params = new HttpParams().set('aggressiveLevel', aggressiveLevel);
    return this.http.post(this.baseUrl, metrics, {
      params,
      responseType: 'text', // Expecting plain text from Hugging Face
    });
  }
}
