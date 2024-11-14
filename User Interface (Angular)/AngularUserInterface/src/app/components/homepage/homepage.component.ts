import {Component, OnDestroy} from '@angular/core';
import {Title} from "@angular/platform-browser";
import {Router} from "@angular/router";
import {PLOT_TYPES} from "../../constants/constants";

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnDestroy{
  title = 'Homepage';
  constructor(private titleService: Title,
              private router : Router) {}

  ngOnInit(): void {
    let token;
    token = localStorage.getItem('accessToken');
    if (token === null) {
      this.router.navigate(['/login']).then(() => {});
      console.log("login");
    } else {
      console.log("homepage");
      this.titleService.setTitle('Homepage');
      this.router.navigate(['homepage/'+PLOT_TYPES.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME]).then(() => {});
    }
  }
  message : string;

  //here you can add the page you want to navigate depending on the tab you are on
  changeTabViaEvent($event : string){
     this.message= $event;
    if (this.message==PLOT_TYPES.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME){
      this.router.navigate(['homepage/'+PLOT_TYPES.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME]).then(() => {});
    }
    if (this.message==PLOT_TYPES.SOHC_3_WORST_CELL_INDEX_AGAINST_TIME){
      this.router.navigate(['homepage/'+PLOT_TYPES.SOHC_3_WORST_CELL_INDEX_AGAINST_TIME]).then(() => {});
    }
    if (this.message==PLOT_TYPES.SOHC_3_WORST_SOHCS_AGAINST_TIME){
      this.router.navigate(['homepage/'+PLOT_TYPES.SOHC_3_WORST_SOHCS_AGAINST_TIME]).then(() => {});
    }
    if (this.message==PLOT_TYPES.SOHC_EQ_PARAMS_AGAINST_TIME){
      this.router.navigate(['homepage/'+PLOT_TYPES.SOHC_EQ_PARAMS_AGAINST_TIME]).then(() => {});
    }
    if (this.message==PLOT_TYPES.CHARGE_PROFILE_FAST_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE){
      this.router.navigate(['homepage/'+PLOT_TYPES.CHARGE_PROFILE_FAST_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE]).then(() => {});
    }
    if (this.message==PLOT_TYPES.CHARGE_PROFILE_PROTECTED_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE){
      this.router.navigate(['homepage/'+PLOT_TYPES.CHARGE_PROFILE_PROTECTED_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE]).then(() => {});
    }

  }

  ngOnDestroy(): void {
    // noinspection TypeScriptValidateTypes
    Object.values(PLOT_TYPES).forEach((value => {
      localStorage.removeItem(value);
      console.log("Destroyed "+value);
    }))
  }



}
