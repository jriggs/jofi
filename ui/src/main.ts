import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent as App } from './app/app';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app/app.routes';

bootstrapApplication(App, {
    ...appConfig,
    providers: [
      ...(appConfig.providers || []),
      provideHttpClient(),
      provideRouter(routes)
    ]
  })
  .catch((err) => console.error(err));
