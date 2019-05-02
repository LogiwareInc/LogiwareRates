import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { RatesService } from '../rates.service';
import { Observable, of } from 'rxjs';
import { TypeaheadMatch } from 'ngx-bootstrap/typeahead';
import { mergeMap } from 'rxjs/operators';
import * as moment from 'moment';
import { Router } from '@angular/router';

@Component({
  selector: 'search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  form: FormGroup;
  isSubmitted = false;
  typeaheadNoResults: boolean;
  carriers: Observable<any>;
  scacs: Observable<any>;
  typeaheadResults: any[] = [];
  results: any[] = [];
  resultCount = 0;

  constructor(
    private formBuilder: FormBuilder,
    private ratesService: RatesService,
    private router: Router
  ) {
    this.carriers = Observable.create((observer: any) => {
      observer.next(this.form.value.carrier);
    }).pipe(mergeMap((input: string) => this.getTypeaheadResults(input, "carrier")));
    this.scacs = Observable.create((observer: any) => {
      observer.next(this.form.value.scac);
    }).pipe(mergeMap((input: string) => this.getTypeaheadResults(input, "scac")));
  }

  ngOnInit() {
    this.form = this.formBuilder.group({
      carrier: new FormControl("", Validators.required),
      scac: new FormControl("", Validators.required),
      fromDate: new FormControl("", Validators.required),
      toDate: new FormControl("", Validators.required)
    });
  }

  getTypeaheadResults(input: string, type: string) {
    this.ratesService.getTypeaheadResults(type, input).then(res => {
      this.typeaheadResults = res;
    });
    return of(
      this.typeaheadResults.filter((state: any) => {
        return true;
      })
    );
  }

  typeaheadOnSelect(e: TypeaheadMatch, otherField: string): void {
    if (otherField === "scac") {
      this.form.patchValue({
        scac: e.item.value
      })
    } else {
      this.form.patchValue({
        carrier: e.item.value
      })
    }
  }

  search() {
    const formData: FormData = new FormData();
    formData.append("carrier", this.form.value.carrier);
    formData.append("fromDate", moment(this.form.value.fromDate).format("L"));
    formData.append("toDate", moment(this.form.value.toDate).format("L"));
    this.ratesService.searchRates(formData).then(res => {
      this.results = res;
      this.resultCount = res.length;
    }).catch(e => {
      console.log(e)
    });
  }

  load() {
    this.router.navigate(["/rates"]);
  }

  clear() {
    this.form.reset();
    this.results = [];
  }

}
