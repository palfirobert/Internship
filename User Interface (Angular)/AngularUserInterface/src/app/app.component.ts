import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {PLOT_TYPES} from "./constants/constants";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit,OnDestroy {
  constructor(private router: Router) {
  }

  ngOnInit(): void {
/*    let token;
    token = localStorage.getItem("accessToken");

    if (token != null) {
      this.router.navigate(['/homepage']);
    } else {
      this.router.navigate(['/login']);
    }*/

    Object.values(PLOT_TYPES).forEach((value => {
      localStorage.removeItem(value);
      console.log("Destroyed "+value);
    }));
  }

  ngOnDestroy(): void {

  }
}
