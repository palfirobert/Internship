import {Component, Input} from '@angular/core';
import {ImageService} from "../../services/image.service";
import {PLOT_TYPES} from "../../constants/constants";

@Component({
  selector: 'app-firsttab',
  templateUrl: './sohc_min_cell_evolution_against_time.component.html',
  styleUrls: ['./sohc_min_cell_evolution_against_time.component.css']
})
export class Sohc_min_cell_evolution_against_timeComponent {

  @Input() plotRequest: any;

  constructor(public imageService: ImageService) {
    this.imageService.plotRequest.plotType = PLOT_TYPES.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME;
  }

}
