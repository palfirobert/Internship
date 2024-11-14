import { Component } from '@angular/core';
import {ImageService} from "../../services/image.service";
import {PLOT_TYPES} from "../../constants/constants";

@Component({
  selector: 'app-charge-profile-protected-current-against-soc-for-each-temperture',
  templateUrl: './charge-profile-protected-current-against-soc-for-each-temperture.component.html',
  styleUrls: ['./charge-profile-protected-current-against-soc-for-each-temperture.component.css']
})
export class ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent {
  constructor(public imageService:ImageService) {
    this.imageService.plotRequest.plotType = PLOT_TYPES.CHARGE_PROFILE_PROTECTED_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE;
  }
}
