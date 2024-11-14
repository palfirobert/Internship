import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatMenuModule} from '@angular/material/menu';
import {MatTabsModule} from '@angular/material/tabs';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSelectModule} from '@angular/material/select';
import {MatIconModule} from "@angular/material/icon";
import {MatGridListModule} from '@angular/material/grid-list';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatChipsModule} from '@angular/material/chips';
import {MatAutocompleteModule} from "@angular/material/autocomplete";


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
  ],
  exports: [
    BrowserAnimationsModule,
    MatSlideToggleModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatToolbarModule,
    MatMenuModule,
    MatTabsModule,
    MatSnackBarModule,
    MatSelectModule,
    MatIconModule,
    MatGridListModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatChipsModule,
    MatAutocompleteModule,
  ],
})
export class MaterialDesignModule { }
