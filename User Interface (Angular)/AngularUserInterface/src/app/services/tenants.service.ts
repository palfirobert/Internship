import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {BACKEND_API_URL} from "../constants/constants";

@Injectable({
  providedIn: 'root'
})
export class TenantsService {

  constructor(private httpClient: HttpClient) {
  }

  /**
   * Requests assigned tenants.
   * @param page - the page from the database the user wants to read from
   * @param pageSize - number of entities to be read
   */
  loadUserTenants(page : number, pageSize : number): Observable<any> {

    return this.httpClient.get(`${BACKEND_API_URL}/v1/tenants?page=${page}&size=${pageSize}`,
      { headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'Authorization' : 'Bearer ' + localStorage.getItem("accessToken")!.toString()
      })});
  }

  /**
   * Requests all tenants.
   */
  loadAllTenants(): Observable<any> {

    return this.httpClient.get(`${BACKEND_API_URL}/v1/tenants/all`,
      { headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
          'Authorization' : 'Bearer ' + localStorage.getItem("accessToken")!.toString()
        })});
  }

  assignTenants(tenants: string[]): Observable<any> {
    const requestBodyJSON = JSON.stringify(tenants);

    return this.httpClient.post<any>(`${BACKEND_API_URL}/v1/tenants/assign`, requestBodyJSON,
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
          'Authorization' : 'Bearer ' + localStorage.getItem("accessToken")!.toString()
        })
      });
  }
}
