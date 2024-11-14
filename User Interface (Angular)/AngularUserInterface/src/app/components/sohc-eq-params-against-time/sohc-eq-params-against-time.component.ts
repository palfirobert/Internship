import { Component } from '@angular/core';
import {ImageService} from "../../services/image.service";
import {PLOT_TYPES} from "../../constants/constants";

@Component({
  selector: 'app-sohc-eq-params-against-time',
  templateUrl: './sohc-eq-params-against-time.component.html',
  styleUrls: ['./sohc-eq-params-against-time.component.css']
})
export class SohcEqParamsAgainstTimeComponent {
  constructor(public imageService:ImageService) {
    this.imageService.plotRequest.plotType = PLOT_TYPES.SOHC_EQ_PARAMS_AGAINST_TIME;
  }

}
