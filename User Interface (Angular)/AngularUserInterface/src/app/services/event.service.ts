import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class EventService {

  constructor() {}

  generatePlotsButtonSubject = new Subject<void>();

  generatePlotsButton$ = this.generatePlotsButtonSubject.asObservable();

  private donePlotsSubject = new Subject<void>();

  donePlots$ = this.donePlotsSubject.asObservable();

  /**
   * An event from header component triggers an event in image component.
   * When generateButton is clicked in header component, getPlots function is called in image component.
   */
  generatePlotsButton() {
    this.generatePlotsButtonSubject.next();

  }

  /**
   * When the plots are done loading (image component), generatePlotsButton is enabled in header component.
   */
  donePlots() {
    this.donePlotsSubject.next();
  }


}
