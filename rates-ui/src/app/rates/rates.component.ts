import { Component, OnInit, TemplateRef, ContentChild, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl, FormArray, ValidatorFn } from '@angular/forms';
import { RatesService } from '../rates.service';
import { Observable, of } from 'rxjs';
import { TypeaheadMatch } from 'ngx-bootstrap/typeahead';
import { mergeMap } from 'rxjs/operators';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import * as moment from 'moment';
import { Router } from '@angular/router';

@Component({
  selector: 'rates',
  templateUrl: './rates.component.html',
  styleUrls: ['./rates.component.css']
})
export class RatesComponent implements OnInit {
  form: FormGroup;
  isSubmitted = false;
  typeaheadNoResults: boolean;
  carriers: Observable<any>;
  scacs: Observable<any>;
  results: any[] = [];
  filename: string;
  modalRef: BsModalRef;
  sites: any[] = [];
  file: File;
  message: string = "";
  @ViewChild("errorTemplate") errorTemplate: TemplateRef<any>;
  errors: any[] = [];
  currencies: string[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private ratesService: RatesService,
    private modalService: BsModalService,
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
    this.filename = "Choose File..";
    this.form = this.formBuilder.group({
      carrier: new FormControl("", Validators.required),
      scac: new FormControl("", Validators.required),
      effectiveDate: new FormControl("", Validators.required),
      expirationDate: new FormControl("", Validators.required),
      surchargeType: new FormControl("CURRENT", Validators.required),
      surchargeCurrency: new FormControl("USD", Validators.required),
      file: new FormControl("", Validators.required),
      sites: new FormArray([])
    });
    this.getOptionResults("currencies");
  }

  getTypeaheadResults(input: string, type: string) {
    this.ratesService.getTypeaheadResults(type, input).then(res => {
      this.results = res;
    });
    return of(
      this.results.filter((state: any) => {
        return true;
      })
    );
  }

  getOptionResults(type: string) {
    this.ratesService.getOptionResults(type).then(res => {
      this.currencies = res;
    });
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

  changeFile(e) {
    let file = e.target.files[0];
    if (file !== undefined && file.name !== "") {
      this.file = file;
      this.filename = file.name.substring(file.name.lastIndexOf('/') + 1);
    } else {
      this.filename = "Choose File..";
      this.file = null;
    }
  }

  openModal(template: TemplateRef<any>) {
    this.modalRef = this.modalService.show(template, { class: 'modal-lg' });
  }

  mapToCheckboxes(): FormArray {
    return this.formBuilder.array(this.sites.map((i) => {
      return this.formBuilder.group({
        name: i,
        selected: true
      });
    }))
  }

  load(template: TemplateRef<any>) {
    this.ratesService.getSites().then(res => {
      this.sites = res;
      this.form.setControl("sites", this.mapToCheckboxes());
      this.openModal(template);
    });
  }

  proceed() {
    console.log(this.form.value)
    const formData: FormData = new FormData();
    formData.append("carrier", this.form.value.carrier);
    formData.append("scac", this.form.value.scac);
    formData.append("effectiveDate", moment(this.form.value.effectiveDate).format("L"));
    formData.append("expirationDate", moment(this.form.value.expirationDate).format("L"));
    formData.append("surchargeType", this.form.value.surchargeType);
    formData.append("surchargeCurrency", this.form.value.surchargeCurrency);
    formData.append("file", this.file, this.file.name);
    let sites = [];
    this.form.controls.sites.value.forEach(e => {
      if (e.selected) {
        sites.push(e.name.key);
      }
    });
    formData.append("sites", sites.join(","));
    this.modalRef.hide();
    this.ratesService.loadRates(formData).then(res => {
      this.message = res.message;
      if (res.errors.length > 0) {
        this.errors = res.errors;
        this.openModal(this.errorTemplate);
      }
      setTimeout(() => {
        this.search();
      }, 1000);
    }).catch(e => {
      console.log(e)
    });
  }

  clear() {
    this.form.reset();
    this.form.patchValue({
      surchargeType: "CURRENT",
      surchargeCurrency: "USD"
    });
    this.filename = "Choose File..";
    this.file = null;
  }

  search() {
    this.router.navigate(["/search"]);
  }

  close() {
    this.modalRef.hide();
  }

}
