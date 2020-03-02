import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { MatTableExporterModule } from 'mat-table-exporter';
import { Ng4LoadingSpinnerModule } from 'ng4-loading-spinner';
// import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { HeaderComponent } from './utility/header/header.component';
import { FooterComponent } from './utility/footer/footer.component';
import { SidebarComponent } from './utility/sidebar/sidebar.component';
import { HomeComponent } from './home/home.component';
import { ConfigComponent } from './config/config.component';
import { RunTestComponent } from './run-test/run-test.component';
import { MaterialModule } from './material/material.module';
import { InterceptorService } from './services/interceptor.service';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    SidebarComponent,
    HomeComponent,
    ConfigComponent,
    RunTestComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    HttpClientModule,
    MatTableExporterModule,
    Ng4LoadingSpinnerModule.forRoot()
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: InterceptorService, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
