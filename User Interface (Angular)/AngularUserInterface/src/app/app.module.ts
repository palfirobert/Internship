import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {ImageService} from "./services/image.service";
import { ImageComponent } from './components/image/image.component';
import { LoginComponent } from './components/login/login.component';
import {LoginService} from "./services/login.service";
import {MaterialDesignModule} from "./material-design/material-design.module";
import { HomepageComponent } from './components/homepage/homepage.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { Sohc_min_cell_evolution_against_timeComponent } from './components/sohc_min_cell_evolution_against_time/sohc_min_cell_evolution_against_time.component';
import { Sohc_3_worst_cell_index_against_timeComponent } from './components/sohc_3_worst_cell_index_against_time/sohc_3_worst_cell_index_against_time.component';
import { Sohc_3_worst_sohcs_against_timeComponent } from './components/sohc_3_worst_sohcs_against_time/sohc_3_worst_sohcs_against_time.component';
import {TenantsService} from "./services/tenants.service";
import { CreateAccountComponent } from './components/create-account/create-account.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RegisterService} from "./services/register.service";
import {ResetPasswordService} from "./services/reset-password.service";
import { VerifyAccountComponent } from './components/verify-account/verify-account.component';
import {TimeoutService} from "./services/timeout.service";
import {TimeoutDialogComponent, TimeoutDialogService} from "./services/timeout-dialog.service";
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SohcEqParamsAgainstTimeComponent } from './components/sohc-eq-params-against-time/sohc-eq-params-against-time.component';
import { ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent } from './components/charge-profile-fast-current-against-soc-for-each-temperature/charge-profile-fast-current-against-soc-for-each-temperature.component';
import { ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent } from './components/charge-profile-protected-current-against-soc-for-each-temperture/charge-profile-protected-current-against-soc-for-each-temperture.component';
import { ResetPasswordPageComponent } from './components/reset-password-page/reset-password-page.component';
import { ProfileComponent } from './components/profile/profile.component';

import {NgFor} from "@angular/common";


@NgModule({
  declarations: [
    AppComponent,
    ImageComponent,
    LoginComponent,
    HomepageComponent,
    HeaderComponent,
    FooterComponent,
    Sohc_min_cell_evolution_against_timeComponent,
    Sohc_3_worst_cell_index_against_timeComponent,
    Sohc_3_worst_sohcs_against_timeComponent,
    SohcEqParamsAgainstTimeComponent,
    ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent,
    ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent,
    TimeoutDialogComponent,
    CreateAccountComponent,
    ResetPasswordComponent,
    VerifyAccountComponent,
    ResetPasswordPageComponent,
    ProfileComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    MaterialDesignModule,
    BrowserAnimationsModule,
    NgbModule,
    FormsModule,
    ReactiveFormsModule,
    NgFor,

  ],

  providers: [ImageService, LoginService, TenantsService,TimeoutService,TimeoutDialogService,RegisterService, ResetPasswordService],
  bootstrap: [AppComponent]
})
export class AppModule { }
