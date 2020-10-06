import { Injectable, NgZone } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpErrorResponse ,HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap, catchError, map, retry } from 'rxjs/operators';

@Injectable()
export class InterceptorService implements HttpInterceptor {


  constructor( private http: HttpClient){}
  
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  
    if (!request.headers.has('Content-Type')) {
      request = request.clone({ setHeaders: { "Content-Type": 'application/json; charset=UTF-8'} });
    }

    if (!request.headers.has('Accept')) {
      request = request.clone({ setHeaders: {"Accept": 'application/json'} });
    }    
    return next.handle(request).pipe( 
      retry(2),
      catchError((error: HttpErrorResponse) => {
        if (error.status == 0) {
          alert('Currently we are facing issue in fetching data please again later');   
        }
        return throwError(error);
      })
    );

  }

}
