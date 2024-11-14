import { Injectable } from '@angular/core';
import {catchError, Observable, tap, throwError} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {BACKEND_API_URL} from "../constants/constants";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private httpClient: HttpClient) { }

  login(email: string, password: string): Observable<any> {

    const loginPayload = { email, password };

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
      })
    };

    return this.httpClient.post<any>(`${BACKEND_API_URL}/v1/authentication/login`, loginPayload, httpOptions).pipe(
      tap(response => {
        // The server should send a JWT token in the 'token' property of the response upon successful login
        if (response.token) {
          localStorage.setItem('accessToken', response.token);
        }
        if (response.sessionTime)
        {
          localStorage.setItem('sessionTime',response.sessionTime);
          console.log(localStorage.getItem('sessionTime'));
        }
      }),
      catchError(error => {
        return throwError(error);
      })
    );
  }

  logout(): void {

    const requestBodyJSON = JSON.stringify(localStorage.getItem('accessToken'));

    this.httpClient.post<any>(`${BACKEND_API_URL}/v1/authentication/logout`, requestBodyJSON,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        })
      });


    // Clear the access token from local storage when logging out
    localStorage.removeItem('accessToken');
    localStorage.removeItem('email');
    localStorage.removeItem('password');
  }

  isAuthenticated(): boolean {
    // Check if the user is authenticated based on the presence of the access token
    const accessToken = localStorage.getItem('accessToken');
    return !!accessToken; // Returns true if the token exists and is not empty
  }
}
