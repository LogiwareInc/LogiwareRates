import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { NgxSpinnerService } from 'ngx-spinner';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
    constructor(private spinner: NgxSpinnerService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        this.spinner.show();
        // add authorization header with jwt token if available
        let ratesToken = localStorage.getItem("rates-token");
        let multipart = localStorage.getItem("multipart");
        if (ratesToken) {
            if (multipart === "true") {
                const cloned = req.clone({
                    headers: new HttpHeaders({
                        "rates-token": ratesToken
                    })
                });
                localStorage.removeItem("multipart")
                return next.handle(cloned).pipe(
                    map((event: HttpEvent<any>) => {
                        if (event instanceof HttpResponse) {
                            this.spinner.hide();
                        }
                        return event;
                    }));
            } else {
                const cloned = req.clone({
                    headers: new HttpHeaders({
                        "rates-token": ratesToken,
                        "Content-Type": "application/json"
                    })
                });
                return next.handle(cloned).pipe(
                    map((event: HttpEvent<any>) => {
                        if (event instanceof HttpResponse) {
                            this.spinner.hide();
                        }
                        return event;
                    }));
            }
        } else {
            return next.handle(req).pipe(
                map((event: HttpEvent<any>) => {
                    if (event instanceof HttpResponse) {
                        this.spinner.hide();
                    }
                    return event;
                }));
        }
    }
}