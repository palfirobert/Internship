import { Component } from '@angular/core';
import {ImageService} from "../../services/image.service";
import {PLOT_TYPES} from "../../constants/constants";

@Component({
  selector: 'app-secondtab',
  templateUrl: './sohc_3_worst_cell_index_against_time.component.html',
  styleUrls: ['./sohc_3_worst_cell_index_against_time.component.css']
})
export class Sohc_3_worst_cell_index_against_timeComponent {
  constructor(public imageService:ImageService) {
    this.imageService.plotRequest.plotType = PLOT_TYPES.SOHC_3_WORST_CELL_INDEX_AGAINST_TIME;
  }
}
