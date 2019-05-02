import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../environments/environment';

const baseUrl = `${environment.apiUrl}`;
@Injectable({
  providedIn: 'root'
})
export class RatesService {

  constructor(private httpClient: HttpClient) { }

  getTypeaheadResults(type: any, input: any) {
    return this.httpClient.get(baseUrl + "/rates/typeahead?type=" + type + "&input=" + encodeURIComponent(input))
      .toPromise().then((response: any) => {
        return response;
      }).catch(this.handleError);
  }

  getOptionResults(type: any) {
    return this.httpClient.get(baseUrl + "/rates/options?type=" + type)
      .toPromise().then((response: any) => {
        return response;
      }).catch(this.handleError);
  }

  getSites() {
    return this.httpClient.get(baseUrl + "/rates/sites")
      .toPromise().then((response: any) => {
        return response;
      }).catch(this.handleError);
  }

  loadRates(formData: FormData) {
    let headers = new HttpHeaders({
      "Accept": "multipart/form-data"
    });
    localStorage.setItem("multipart", "true");
    let params = new HttpParams();
    return this.httpClient.post(baseUrl + "/rates/load", formData, { params, headers })
      .toPromise().then((response: any) => {
        return response;
      }).catch(this.handleError);
  }

  searchRates(formData: FormData) {
    let headers = new HttpHeaders({
      "Accept": "multipart/form-data"
    });
    localStorage.setItem("multipart", "true");
    let params = new HttpParams();
    return this.httpClient.post(baseUrl + "/rates/search", formData, { params, headers })
      .toPromise().then((response: any) => {
        return response;
      }).catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.log(error)
    return Promise.reject(error || error);
  }
}
