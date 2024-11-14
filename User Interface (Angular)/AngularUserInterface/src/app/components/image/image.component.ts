import {Component, Input, OnInit} from '@angular/core';
import {ImageService} from "../../services/image.service";
import {EventService} from "../../services/event.service";
import {Subscription} from "rxjs";


@Component({
  selector: 'image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css']
})
export class ImageComponent implements OnInit {

  isImageLoading = false;
  @Input() imagesToShow: Array<any> = [];
  noPlots = true;
  zoomedPlot: any;
  batteriesId: Array<any> = [];
  subscription: Subscription;
  cp_index: number;


  constructor(public imageService: ImageService,
              private eventService: EventService) {

    // VERIFY IF THERE ARE PREVIOUS PLOTS ON THE CURRENT TAB AND IF TRUE SHOWS THEM
    if (localStorage.getItem(this.imageService.currentTab) != null) {
      this.imagesToShow = JSON.parse(<string>localStorage.getItem(this.imageService.currentTab));
      this.batteriesId = JSON.parse(<string>localStorage.getItem("batteriesId" + this.imageService.currentTab));
      this.noPlots = false;
      this.cp_index = this.imagesToShow.length / this.batteriesId.length;
      this.zoomedPlot = this.imagesToShow[0];
      this.isImageLoading = false;
    }

    this.subscription = this.eventService.generatePlotsButton$.subscribe(() => {
      this.getBatteriesId();
      this.getPlots();
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  /**
   * Calls imageService to retrieve the plots.
   */
  getPlots() {

    localStorage.removeItem(this.imageService.currentTab);
    localStorage.removeItem("batteriesId" + this.imageService.currentTab);
    this.noPlots = false;
    this.isImageLoading = true;

    this.imagesToShow = [];

    this.imageService.getPlot().subscribe({
      next: data => {
        for (const key in data) {
          this.imagesToShow.push(this.decodeImage(data[key]));
        }

        // GET THE INDEX IF PLOTTING MULTIPLE PLOTS FOR ONE BATTERY FOR CP
        this.cp_index = this.imagesToShow.length / this.batteriesId.length;

        // SET THE NEW PLOTS IN LOCAL STORAGE TO BE SHOWN IF GOING BACK TO THE TAB
        localStorage.setItem(this.imageService.currentTab, JSON.stringify(this.imagesToShow));
        localStorage.setItem("batteriesId" + this.imageService.currentTab, JSON.stringify(this.batteriesId));
        this.zoomedPlot = this.imagesToShow[0];
      },
      error: error => {
        console.log(this.imagesToShow);
        console.log(this.batteriesId);
        console.log(this.imageService.plotRequest);
        this.isImageLoading = false;
        console.log(error);
      },
      complete: () => {
        this.isImageLoading = false;
        this.eventService.donePlots();
      }
    });
  }

  /**
   * Gets the ids of the batteries the user requested plots for.
   */
  getBatteriesId() {
    this.batteriesId = this.imageService.getBatteriesId();
  }


  /**
   * @param base64Image - image in base64
   * Decodes the image and creates a Blob object with the decoded bytes.
   * Returns URL for the blob, which can be used as the image source.
   */
  decodeImage(base64Image: string) {
    const binaryString = atob(base64Image);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);

    for (let i = 0; i < len; ++i) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    const blob = new Blob([bytes], {type: 'image/jpeg'});

    return URL.createObjectURL(blob);
  }

  /**
   * @param key - the plot user wants to see zoomed
   * Changes the zoomPlot to the plot selected by the user from the gallery.
   */
  onImageClick(key: any) {
    this.zoomedPlot = key;
  }

  ngOnInit(): void {
  }

  logBatteryId(i: number) {
    console.log(i);
    console.log(this.cp_index);
    const index = i / this.cp_index;
    const batteryId = this.batteriesId[Math.floor(index)];
    console.log(Math.floor(index));
    return batteryId;
  }

}
