import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { HttpResponse } from '@angular/common/http';
import { first } from 'rxjs/operators';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  isSubmitted = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  get formControls() { return this.loginForm.controls; }

  login() {
    this.isSubmitted = true;
    if (this.loginForm.invalid) {
      return;
    }
    this.authService.login(this.loginForm.value).pipe(first())
      .subscribe(
        (data: HttpResponse<any>) => {
          localStorage.removeItem("rates-token");
          localStorage.setItem("rates-token", data.headers.get('rates-token'));
          localStorage.setItem('logo', data.body.logo)
          this.router.navigate(["/rates"]);
        },
        error => {
          this.isSubmitted = false;
        });

  }

}
