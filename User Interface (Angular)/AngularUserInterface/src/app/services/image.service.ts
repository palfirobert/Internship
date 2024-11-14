import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {BACKEND_API_URL, PLOT_TYPES} from "../constants/constants";

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  public plotRequest = {tenantId: '', batteryIds: Array(), plotType: ''}
  public currentTab: any;

  constructor(private httpClient: HttpClient) {
    this.currentTab = PLOT_TYPES.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME;
  }

  /**
   * Calls the backend to get the plots.
   */
  getPlot() :Observable<any> {
    const requestBodyJSON = JSON.stringify(this.plotRequest);
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'Authorization': 'Bearer ' + localStorage.getItem("accessToken")!.toString(),
        'responseType': 'blob'
      })
    };

    return this.httpClient.post<any>(`${BACKEND_API_URL}/blob/plots`, requestBodyJSON, httpOptions);
  }

  /**
   * Return batteries id the user selected to see plots for.
   */
  getBatteriesId() {
    return this.plotRequest.batteryIds;
  }
}

