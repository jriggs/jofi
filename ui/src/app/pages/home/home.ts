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
  selectedSymbol: string = '';

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

  onTickerChange(symbol: string) {
    this.selectedSymbol = symbol;
    if (symbol) {
      this.loadStockData(symbol);
    } else {
      this.chartSeries = [];
      this.chartXAxis = { categories: [] };
      this.chartTitle = { text: 'Stock Price' };
    }
  }

  loadStockData(symbol: string) {
    this.stockService.getStockData(symbol).subscribe({
      next: (data: StockDailyData[]) => {
        const closePrices = data.map((d) => d.close);
        const maxClose = Math.max(...closePrices);
        const minClose = Math.min(...closePrices);
        // get name of the symbol
        const companyName = this.tickers.find((t) => t.symbol === symbol);
        const percentChange =
          ((closePrices[closePrices.length - 1] - closePrices[0]) /
            closePrices[0]) *
          100;
        this.chartSeries = [
          {
            name: symbol,
            data: closePrices,
          },
        ];
        this.chartXAxis = {
          categories: data.map((d) => d.date),
        };
        this.chartTitle = {
          text: `${
            companyName?.name || symbol
          } Close Price (1mo) Gain/Loss: ${percentChange.toFixed(
            2
          )}%  Max: ${maxClose.toFixed(2)} Min: ${minClose.toFixed(2)}`,
        };
      },
      error: (err) => console.error('API error (stock data):', err),
    });
  }
}
