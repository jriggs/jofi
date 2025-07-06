import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  NgApexchartsModule,
  ApexChart,
  ApexAxisChartSeries,
  ApexXAxis,
  ApexTitleSubtitle,
} from 'ng-apexcharts';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { StockService } from '../../services/stock';
import { Ticker } from '../../models/ticker';
import { StockDailyData } from '../../models/stockData';
import { TickerService } from '../../services/ticker';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    NgApexchartsModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    FormsModule,
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
})
export class HomeComponent implements OnInit {
  tickers: Ticker[] = [];
  selectedSymbols: string[] = [];

  chartSeries: ApexAxisChartSeries = [];
  chartDetails: ApexChart = {
    type: 'line',
    height: 350,
    zoom: { enabled: false },
    toolbar: { show: false },
    animations: { enabled: false },
  };
  chartXAxis: ApexXAxis = { categories: [] };
  chartTitle: ApexTitleSubtitle = { text: 'Stock Price' };
  chartYAxis: any = {
    labels: {
      formatter: (val: number) => val.toFixed(2),
    },
  };

  private stockDataCache: Map<string, StockDailyData[]> = new Map();

  constructor(
    private tickerService: TickerService,
    private stockService: StockService
  ) {}

  ngOnInit() {
    this.loadTickers();
  }

  loadTickers() {
    this.tickerService.getAll().subscribe({
      next: (data) => (this.tickers = data),
      error: (err) => console.error('API error (tickers):', err),
    });
  }

  onTickersChange() {
    if (this.selectedSymbols.length) {
      this.loadMultipleStockData(this.selectedSymbols);
    } else {
      this.chartSeries = [];
      this.chartXAxis = { categories: [] };
      this.chartTitle = { text: 'Stock Price' };
    }
  }

  async loadMultipleStockData(symbols: string[]) {
    try {
      const series: ApexAxisChartSeries = [];
      let xCategories: string[] = [];
      let titleParts: string[] = [];

      for (const symbol of symbols) {
        let data: StockDailyData[];

        if (this.stockDataCache.has(symbol)) {
          data = this.stockDataCache.get(symbol)!;
        } else {
          data = await firstValueFrom(this.stockService.getStockData(symbol));
          this.stockDataCache.set(symbol, data);
        }

        const closePrices = data.map((d) => d.close);
        const companyName = this.tickers.find((t) => t.symbol === symbol)?.name || symbol;
        const percentChange =
          ((closePrices[closePrices.length - 1] - closePrices[0]) / closePrices[0]) * 100;

        series.push({
          name: symbol,
          data: closePrices,
        });

        if (xCategories.length === 0) {
          xCategories = data.map((d) => d.date);
        }

        titleParts.push(`${companyName}: ${percentChange.toFixed(2)}%`);
      }

      this.chartSeries = series;
      this.chartXAxis = { categories: xCategories };
      this.chartTitle = {
        text: `Stock Comparison – ${titleParts.join(' | ')}`,
      };
    } catch (err) {
      console.error('API error (multi-stock):', err);
    }
  }

  clearCache() {
    this.stockDataCache.clear();
    this.onTickersChange();
  }
}
