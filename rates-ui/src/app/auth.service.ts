import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from './user';
import { environment } from '../environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

const baseUrl = `${environment.apiUrl}`;

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  observe: 'response' as 'response'
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;
  private user = new User();


  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User {
    return this.currentUserSubject.value;
  }

  refresh(token: string) {
    let refreshOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json', 'rates-token': token }),
      observe: 'response' as 'response'
    };
    return this.http.post<any>(baseUrl + '/auth/refresh', '', refreshOptions)
      .pipe(map(user => {
        // refresh successful if there's a jwt token in the response
        if (user) {
          this.user = new User();
          this.user.firstName = user.body.firstName;
          this.user.roleName = user.body.roleName;
          localStorage.setItem('currentUser', JSON.stringify(user));
          this.currentUserSubject.next(this.user);
        } else {
          throw "Invalid User, Please try Again";
        }
        return user;
      }));
  }

  login(user: User) {
    return this.http.post<any>(baseUrl + '/auth/login', { username: user.username, password: user.password }, httpOptions)
      .pipe(map(user => {
        // login successful if there's a jwt token in the response
        if (user) {
          this.user = new User();
          this.user.firstName = user.body.firstName;
          this.user.roleName = user.body.roleName;
          localStorage.setItem('currentUser', JSON.stringify(user));
          this.currentUserSubject.next(this.user);
        } else {
          throw "Invalid User, Please try Again";
        }
        return user;
      }));
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    localStorage.removeItem('rates-token');
    this.currentUserSubject.next(null);
  }

  isLoggedIn() {
    return this.currentUserValue ? true : false;
  }

}
