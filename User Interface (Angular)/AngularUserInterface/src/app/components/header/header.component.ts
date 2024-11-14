import {Component, EventEmitter, Output} from '@angular/core';
import {MatTabChangeEvent} from "@angular/material/tabs";
import {ImageService} from "../../services/image.service";
import {PLOT_TYPES} from "../../constants/constants";
import {LoginService} from "../../services/login.service";
import {Router} from "@angular/router";
import {TenantsService} from "../../services/tenants.service";
import {Tenant} from "../../models/Tenant"
import {BatteriesService} from "../../services/batteries.service";
import {EventService} from "../../services/event.service";
import {TimeoutService} from "../../services/timeout.service";
import {Subscription} from "rxjs";
import {TimeoutDialogService} from "../../services/timeout-dialog.service";


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']

})
export class HeaderComponent {

  public tabsNames = PLOT_TYPES;
  private _idleTimerSubscription: Subscription;
  //how mny entities to be loaded at once
  private pageSize = 20;

  //current page to read from
  private pageTenants = 0;
  private pageBatteries = 0;

  //total pages in database
  private totalPagesTenants = 1;
  private totalPagesBatteries = 1;

  //user`s tenants
  tenants: Tenant[] = [];
  //tenant`s batteries
  batteries: string[] = [];

  //user`s selection
  selectedTenant: Tenant;
  selectedBatteries: string[] = [];

  isLoadingTenants = false;
  isLoadingBatteries = false;

  disableTenantsDropdown = false;
  disableBatteriesDropdown = true;
  disableGenerateButton = true;
  disableTabs = false;


  @Output() tabChanged: EventEmitter<string> = new EventEmitter<string>();


  constructor(public imageService: ImageService,
              public loginService: LoginService,
              private router: Router,
              public tenantsService: TenantsService,
              public batteriesService: BatteriesService,
              private eventService: EventService
    , private timeoutService: TimeoutService, private timeoutDialogService: TimeoutDialogService) {
    this.eventService.donePlots$.subscribe(() => {
      this.disableTenantsDropdown = false;
      this.disableBatteriesDropdown = false;
      this.disableGenerateButton = false;
      this.disableTabs = false;
    });
  }

  ngOnInit() {
    this.loadTenants();
    this.sessionTimeout();
  }

  //function that recives a tab reffrence and anounces the parrent that he sould change the view from router smtghing
  sendTabChangeEventToParrent(matTabChangeEvent: MatTabChangeEvent) {
    window.history.replaceState({}, '', `/#${matTabChangeEvent.index}`);
    this.tabChanged.emit(matTabChangeEvent.tab.textLabel);
    this.imageService.plotRequest = {tenantId: '', batteryIds: Array(), plotType: ''};
    this.imageService.currentTab = (matTabChangeEvent.tab.textLabel).toString();

  }

  /**
   * Loads tenants paginated until last page is reached.
   */
  loadTenants() {
    if (this.pageTenants <= this.totalPagesTenants) {
      this.isLoadingTenants = true;
      this.tenantsService.loadUserTenants(this.pageTenants, this.pageSize).subscribe({
        next: data => {
          for (const key in data.content) {
            this.tenants.push(data.content[key].tenantId);
            this.totalPagesTenants = data.totalPages;
            this.pageTenants++;
          }
        },
        error: error => {
          console.log(error);
          this._idleTimerSubscription.unsubscribe();
        },
        complete: () => {
          this.isLoadingTenants = false;
        }
      });
    }
  }

  /**
   * Loads batteries paginated until last page is reached.
   */
  loadBatteries() {
    if (this.pageBatteries <= this.totalPagesBatteries) {
      this.isLoadingBatteries = true;
      this.batteriesService.loadBatteries(String(this.selectedTenant), this.pageBatteries, this.pageSize)
        .subscribe({
          next: data => {
            for (const key in data.content) {
              this.batteries.push(data.content[key]);
              this.totalPagesBatteries = data.totalPages;
              this.pageBatteries++;
            }
          },
          error: error => {
            this._idleTimerSubscription.unsubscribe();
            console.log(error);
          },
          complete: () => {
            this.isLoadingBatteries = false;
          }
        });
    }
  }

  /**
   * Initializes the plot request form imageService with values selected by the user.
   * Triggers event in imageComponent to generate the plots
   */
  getDataForRequest() {
    if (this.imageService.plotRequest.batteryIds != this.selectedBatteries) {
      localStorage.removeItem(this.imageService.currentTab);
      localStorage.removeItem("batteriesId");
      console.log(this.imageService.currentTab);
      this.imageService.plotRequest.tenantId = String(this.selectedTenant);
      this.imageService.plotRequest.batteryIds = this.selectedBatteries;
      this.eventService.generatePlotsButton();

      this.disableTenantsDropdown = true;
      this.disableBatteriesDropdown = true;
      this.disableGenerateButton = true;
      this.disableTabs = true;
    }
  }

  /**
   * Navigates user to login page
   */
  logout() {
    this.loginService.logout();
    this._idleTimerSubscription.unsubscribe();
    this.router.navigate(['/login']).then(() => {});
  }

  /**
   * Get selected tenant.
   * Enable batteries dropdown.
   * @param value - selected tenant the user wants to see batteries of
   */
  onSelectedTenant(value: Tenant): void {
    this.selectedTenant = value;
    this.disableBatteriesDropdown = false;
    this.batteries = [];
    this.loadBatteries()
  }

  /**
   * Get selected batteries.
   * @param selectedBatteries - list of selected batteries the user wants to generate plots for
   */
  onSelectedBatteries(selectedBatteries: string[]) {
    this.selectedBatteries = selectedBatteries;
    this.enableGenerateButton();
  }

  /**
   * Generate button disabled if no selected batteries, enabled otherwise.
   */
  enableGenerateButton() {
    this.disableGenerateButton = this.selectedBatteries.length === 0;
  }

  sessionTimeout() {

    if (this._idleTimerSubscription) {
      this._idleTimerSubscription.unsubscribe();
    }

    this.timeoutService.resetTimer();
    this._idleTimerSubscription = this.timeoutService.timeoutExpired.subscribe(() => {
      this.timeoutDialogService.open('Session Expiring!',
        'Your session is about to expire. Do you need more time?', true, 'Yes', 'No')
        .then(resp => {
          if (resp === true) {
            console.log('Extending session...');
            this.timeoutService.resetTimer();
            this.loginService.login(<string>localStorage.getItem('email'), <string>localStorage.getItem('password')).subscribe();

          } else {
            console.log('Not extending session...');
            this.logout();

          }
        })
        .catch(reason => {
          console.log('Dismissed ' + reason);
          this.logout();
        });
    });
  }

  profile() {
    this.router.navigate(['/profile']).then(() => {});
  }
}
