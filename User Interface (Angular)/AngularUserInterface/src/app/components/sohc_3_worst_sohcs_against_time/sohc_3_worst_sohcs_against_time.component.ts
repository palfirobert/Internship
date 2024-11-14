import {Component, Input} from '@angular/core';
import {ImageService} from "../../services/image.service";
import {PLOT_TYPES} from "../../constants/constants";

@Component({
  selector: 'app-thirdtab',
  templateUrl: './sohc_3_worst_sohcs_against_time.component.html',
  styleUrls: ['./sohc_3_worst_sohcs_against_time.component.css']
})
export class Sohc_3_worst_sohcs_against_timeComponent {
  @Input() plotRequest: any;

  constructor(public imageService: ImageService) {
    this.imageService.plotRequest.plotType = PLOT_TYPES.SOHC_3_WORST_SOHCS_AGAINST_TIME;
  }
}
