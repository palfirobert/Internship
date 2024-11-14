import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {BACKEND_API_URL} from "../constants/constants";

@Injectable({
  providedIn: 'root'
})
export class BatteriesService {

  constructor(private httpClient: HttpClient) { }


  /**
   * Requests the batteries assigned to a tenant.
   * @param tenantId - id of the tenant user requested batteries of
   * @param page - the page from the database the user wants to read from
   * @param pageSize - number of entities to be read
   */
  loadBatteries(tenantId : string, page : number, pageSize : number): Observable<any> {

    return this.httpClient.get(`${BACKEND_API_URL}/v1/battery/tenant?tenantId=${tenantId}&page=${page}&size=${pageSize}`,
      { headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
          'Authorization' : 'Bearer ' + localStorage.getItem("accessToken")!.toString()
        })});
  }
}
