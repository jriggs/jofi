import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { TickerService } from '../../services/ticker';
import { Ticker } from '../../models/ticker';

@Component({
  selector: 'app-edit',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './edit.html',
  styleUrls: ['./edit.css'],
})
export class EditComponent implements OnInit {
  tickers: Ticker[] = [];
  newTicker: Ticker = {
    id: 0,
    symbol: '',
    name: '',
    description: '',
    aggressiveLevel: '',
  };
  displayedColumns = [
    'symbol',
    'name',
    'description',
    'aggressiveLevel',
    'actions',
  ];

  constructor(private tickerService: TickerService) {}

  ngOnInit() {
    this.loadTickers();
  }

  loadTickers() {
    this.tickerService
      .getAll()
      .subscribe((data: Ticker[]) => (this.tickers = data));
  }

  save(ticker: Ticker) {
    if (ticker.id !== undefined) {
      this.tickerService.update(ticker.id, ticker).subscribe();
    } else {
      console.error('Ticker id is undefined. Cannot update.');
    }
  }

  add() {
    // Remove id before sending to backend
    const { id, ...payload } = this.newTicker;

    this.tickerService.create(payload as Ticker).subscribe({
      next: (created) => {
        // Add the new ticker to the grid
        this.tickers = [...this.tickers, created]; // triggers change detection
        // Reset the form
        this.newTicker = {
          symbol: '',
          name: '',
          description: '',
          aggressiveLevel: '',
        };
      }
    });
  }

  delete(id: number) {
    this.tickerService.delete(id).subscribe(() => {
      this.tickers = this.tickers.filter((t) => t.id !== id);
    });
  }
}
