import {Component, ElementRef, inject, ViewChild} from '@angular/core';
import {Router} from "@angular/router";
import {Title} from "@angular/platform-browser";
import {map, Observable, startWith} from "rxjs";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {FormControl} from "@angular/forms";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {MatChipInputEvent} from "@angular/material/chips";
import { MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {TenantsService} from "../../services/tenants.service";
import {EventService} from "../../services/event.service";



@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent {
  familyName: string | null;
  givenName: string  | null;
  email: string | null;

  tenants: string[] = [];
  initialTenants: string[] = [];
  separatorKeysCodes: number[] = [ENTER, COMMA];
  filteredTenants: Observable<string[]>;
  tenantCtrl = new FormControl('');
  allTenants: string[] = [];

  @ViewChild('tenantInput') tenantInput: ElementRef<HTMLInputElement>;

  announcer = inject(LiveAnnouncer);
  showMessage: boolean = false;
  successMessage = "Tenants assigned successfully."

  showError: boolean = false;
  errorMessage = ""

  constructor(private router: Router, private titleService: Title, private tenantsService : TenantsService, private eventService: EventService) {

    this.email = localStorage.getItem('email');
    this.familyName = localStorage.getItem('familyName');
    this.givenName = localStorage.getItem('givenName');

    this.loadUserTenants();
    this.loadAllTenants();

  }


  ngOnInit(): void {
    let token;
    token = localStorage.getItem('accessToken');
    if (token === null) {
      this.router.navigate(['/login']).then(() => {});
    } else {
      this.titleService.setTitle('Profile');
      this.router.navigate(['profile']).then(() => {});

    }
  }

  homepage() {
    this.router.navigate(['/homepage']).then(() => {});
  }

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    if (value && !this.tenants.includes(value)) {
      this.tenants.push(value);
    }

    // Clear the input value
    event.chipInput!.clear();

    this.tenantCtrl.setValue(null);
  }

  remove(tenant: string): void {
    const index = this.tenants.indexOf(tenant);

    if (index >= 0) {
      this.tenants.splice(index, 1);

      this.announcer.announce(`Removed ${tenant}`).then(() => {});
    }
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    if(!this.tenants.includes(event.option.viewValue)) {
      this.tenants.push(event.option.viewValue);
    }
    this.tenantInput.nativeElement.value = '';
    this.tenantCtrl.setValue(null);
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.allTenants.filter(fruit => fruit.toLowerCase().includes(filterValue));
  }

  /**
   * Loads tenants paginated until last page is reached.
   */
  loadUserTenants() {
      this.tenantsService.loadUserTenants(0, 80).subscribe({
        next: data => {
          for (const key in data.content) {
            this.tenants.push(data.content[key].tenantId);
            this.initialTenants.push(data.content[key].tenantId);
          }
        },
        error: error => {
          console.log(error);
        }
      });
    }

  /**
   * Loads tenants paginated until last page is reached.
   */
  loadAllTenants() {
    this.tenantsService.loadAllTenants().subscribe({
      next: data => {
        for (const key in data.content) {
          this.allTenants.push(data.content[key].tenantId);
        }
      },
      error: error => {
        console.log(error);
      },
      complete: () => {
        this.filteredTenants = this.tenantCtrl.valueChanges.pipe(
          startWith(null),
          map((tenant: string | null) => (tenant ? this._filter(tenant) : this.allTenants.slice())),
        );
      }
    });
  }

  getNewTenants(){
    let newTenants = [];
    for(const key in this.tenants){
      if(!this.initialTenants.includes(this.tenants[key])){
        newTenants.push(this.tenants[key]);
      }
    }
    return newTenants;
  }

  onSave() {
    let newTenants = this.getNewTenants();
    console.log(newTenants);
    if(newTenants.length!=0){
      this.tenantsService.assignTenants(newTenants).subscribe({
        error: error => {
          this.showError = true;
          this.errorMessage = error.error.text;
        },
        complete: () => {
          this.showMessage = true;
        }
      });
    }
  }

}
