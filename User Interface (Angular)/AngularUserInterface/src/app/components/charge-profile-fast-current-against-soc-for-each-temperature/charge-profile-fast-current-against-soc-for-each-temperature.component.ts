import { Component } from '@angular/core';
import {ImageService} from "../../services/image.service";
import {PLOT_TYPES} from "../../constants/constants";

@Component({
  selector: 'app-charge-profile-fast-current-against-soc-for-each-temperature',
  templateUrl: './charge-profile-fast-current-against-soc-for-each-temperature.component.html',
  styleUrls: ['./charge-profile-fast-current-against-soc-for-each-temperature.component.css']
})
export class ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent {
  constructor(public imageService:ImageService) {
    this.imageService.plotRequest.plotType = PLOT_TYPES.CHARGE_PROFILE_FAST_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE;
  }
}
