import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {BACKEND_API_URL} from "../constants/constants";
import {BatteryUserNew} from "../models/BatteryUserNew";


@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  newUser : BatteryUserNew = {familyName: '', givenName: '', email:'', password:''}

  constructor(private httpClient: HttpClient) { }

  register(): Observable<any> {

    const requestBodyJSON = JSON.stringify(this.newUser);
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'responseType': 'blob'
      })
    };

    return this.httpClient.post<any>(`${BACKEND_API_URL}/v1/authentication/register`, requestBodyJSON, httpOptions);
  }

  verify(token: string | null) :Observable<any>{
    const requestBodyJSON = JSON.stringify(token);

    return this.httpClient.post<any>(`${BACKEND_API_URL}/v1/authentication/verify`, requestBodyJSON,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        })
      });
  }
}
