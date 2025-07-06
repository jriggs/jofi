/*
import { Routes } from '@angular/router';
import { AboutComponent } from './pages/about/about';
import { HomeComponent } from './pages/home/home';
import { EditComponent } from './pages/edit/edit';
import { AnalysisComponent } from './pages/analysis/analysis';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'about', component: AboutComponent },
  { path: 'edit', component: EditComponent },
  { path: 'analysis', component: AnalysisComponent }
];
*/
// src/app/routes.ts
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home').then(m => m.HomeComponent),
  },
  {
    path: 'analysis',
    loadComponent: () => import('./pages/analysis/analysis').then(m => m.AnalysisComponent),
  },
  {
    path: 'edit',
    loadComponent: () => import('./pages/edit/edit').then(m => m.EditComponent),
  },
  {
    path: 'about',
    loadComponent: () => import('./pages/about/about').then(m => m.AboutComponent),
  },
  {
    path: '**',
    redirectTo: '',
  }
];
