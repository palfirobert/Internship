import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {BACKEND_API_URL} from "../constants/constants";

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {

  constructor(private httpClient: HttpClient) { }

  sendEmailResetPassword(email: string):Observable<any>{

    const data = { email : email}
    const requestBodyJSON = JSON.stringify(data);
    console.log(requestBodyJSON);
    return this.httpClient.post<any>(`${BACKEND_API_URL}/v1/authentication/request-reset-password`, requestBodyJSON,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        })
      });
  }

  resetPassword(token: string | null, password: string):Observable<any> {
    const data = {
      token: token,
      password: password
    };

    const requestBodyJSON = JSON.stringify(data);

    return this.httpClient.post<any>(`${BACKEND_API_URL}/v1/authentication/reset-password`, requestBodyJSON,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        })
      });
  }

}
