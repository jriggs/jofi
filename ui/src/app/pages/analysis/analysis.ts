import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockService } from '../../services/stock';
import { TickerService } from '../../services/ticker';
import { StockDailyData } from '../../models/stockData';
import { StockMetric } from '../../models/stockMetric';
import { AnalysisService } from '../../services/analysis';

import {
  ApexAxisChartSeries,
  ApexChart,
  ApexXAxis,
  ApexYAxis,
  ApexTitleSubtitle,
  ApexDataLabels,
  ApexTooltip,
  NgApexchartsModule,
} from 'ng-apexcharts';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-analysis',
  standalone: true,
  imports: [
    CommonModule,
    NgApexchartsModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './analysis.html',
  styleUrls: ['./analysis.css'],
})
export class AnalysisComponent implements OnInit {
  levels: string[] = [];
  selectedLevel: string | null = null;
  loading = false;
  aiLoading = false;
  aiResult = '';
  showPercentChange = true;

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
  chartYAxis: ApexYAxis = {
    labels: {
      formatter: (val: number) => val.toFixed(2),
    },
  };
  chartTooltip: ApexTooltip = {
    shared: true,
    y: {
      formatter: (val: number) => val.toFixed(2),
    },
  };

  barChartSeries: ApexAxisChartSeries = [];
  barChartDetails: ApexChart = {
    type: 'bar',
    height: 350,
    toolbar: { show: false },
    animations: { enabled: false },
  };
  barChartXAxis: ApexXAxis = { categories: [] };
  barChartYAxis: ApexYAxis = {
    labels: {
      formatter: (val: number) => `$${val.toFixed(2)}`,
    },
  };
  barChartTitle: ApexTitleSubtitle = { text: 'Stock Metrics (High, Low, First, Last)' };
  barChartDataLabels: ApexDataLabels = {
    enabled: false,
  };

  private stockDataCache: Record<string, Record<string, StockDailyData[]>> = {};
  private metricCache: Record<string, StockMetric[]> = {};

  constructor(
    private stockService: StockService,
    private tickerService: TickerService,
    private analysisService: AnalysisService
  ) {}

  ngOnInit() {
    this.tickerService.getAggressionLevels().subscribe({
      next: (levels) => (this.levels = levels),
      error: (err) => console.error('Error loading levels:', err),
    });
  }

  toggleChartMode() {
    this.showPercentChange = !this.showPercentChange;
    if (this.selectedLevel && this.stockDataCache[this.selectedLevel]) {
      this.updateChartFromData(this.stockDataCache[this.selectedLevel]);
    }
  }

  selectLevel(level: string) {
    this.selectedLevel = level;
    this.aiResult = '';

    if (this.stockDataCache[level]) {
      this.updateChartFromData(this.stockDataCache[level]);
      this.updateBarChart(this.metricCache[level] || []);
      return;
    }

    this.loading = true;

    this.stockService.getStockByAggression(level).subscribe({
      next: (response) => {
        this.stockDataCache[level] = response.stockData;
        this.metricCache[level] = response.metrics;
        this.updateChartFromData(response.stockData);
        this.updateBarChart(response.metrics);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading stock data:', err);
        this.loading = false;
      },
    });
  }

  private updateChartFromData(data: Record<string, StockDailyData[]>) {
    const allDates = new Set<string>();
    const series: ApexAxisChartSeries = [];

    for (const symbol in data) {
      const entries = data[symbol];
      if (Array.isArray(entries)) {
        entries.forEach((d) => allDates.add(d.date));
      }
    }

    const sortedDates = Array.from(allDates).sort();

    for (const symbol in data) {
      const entries = data[symbol];
      const dateToValue = new Map<string, number>();

      for (const entry of entries) {
        const value = this.showPercentChange
          ? entry.percentChange ?? null
          : entry.close;
        dateToValue.set(entry.date, value);
      }

      const alignedData = sortedDates.map(
        (date) => dateToValue.get(date) ?? null
      );

      series.push({ name: symbol, data: alignedData });
    }

    this.chartSeries = series;
    this.chartXAxis.categories = sortedDates;
    this.chartTitle = {
      text: this.showPercentChange
        ? 'Daily % Change for Selected Stocks'
        : 'Stock Price for Selected Stocks',
    };

    // 🔑 Update Y-axis and tooltip formatting based on mode
    this.chartYAxis = {
      labels: {
        formatter: (val: number) =>
          this.showPercentChange
            ? `${val.toFixed(2)}%`
            : val.toFixed(2),
      },
    };

    this.chartTooltip = {
      shared: true,
      y: {
        formatter: (val: number) =>
          this.showPercentChange
            ? `${val.toFixed(2)}%`
            : val.toFixed(2),
      },
    };
  }

  private updateBarChart(metrics: StockMetric[]) {
    const categories = metrics.map((m) => {
      const percent = (m.percentReturn * 100).toFixed(2);
      return `${m.symbol} (${percent}%)`;
    });

    this.barChartSeries = [
      { name: 'High', data: metrics.map((m) => +m.high.toFixed(2)) },
      { name: 'Low', data: metrics.map((m) => +m.low.toFixed(2)) },
      { name: 'First', data: metrics.map((m) => +m.first.toFixed(2)) },
      { name: 'Last', data: metrics.map((m) => +m.last.toFixed(2)) },
    ];

    this.barChartXAxis.categories = categories;
  }

  runAiAnalysis() {
    if (!this.selectedLevel) return;

    this.aiLoading = true;
    this.aiResult = '';

    this.stockService.getStockByAggression(this.selectedLevel).subscribe({
      next: (response) => {
        const metrics: StockMetric[] = response.metrics;

        this.analysisService.analyze(metrics, this.selectedLevel!).subscribe({
          next: (result) => {
            this.aiResult = result;
            this.aiLoading = false;
          },
          error: (err) => {
            console.error('AI analysis failed:', err);
            this.aiResult = 'Error analyzing metrics.';
            this.aiLoading = false;
          },
        });
      },
      error: (err) => {
        console.error('Error fetching metrics for AI:', err);
        this.aiLoading = false;
      },
    });
  }
}
