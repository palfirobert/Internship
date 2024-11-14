import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {HomepageComponent} from "./components/homepage/homepage.component";
import {AppComponent} from "./app.component";
import {Sohc_min_cell_evolution_against_timeComponent} from "./components/sohc_min_cell_evolution_against_time/sohc_min_cell_evolution_against_time.component";
import {Sohc_3_worst_cell_index_against_timeComponent} from "./components/sohc_3_worst_cell_index_against_time/sohc_3_worst_cell_index_against_time.component";
import {Sohc_3_worst_sohcs_against_timeComponent} from "./components/sohc_3_worst_sohcs_against_time/sohc_3_worst_sohcs_against_time.component";
import {PLOT_TYPES} from "./constants/constants";
import {SohcEqParamsAgainstTimeComponent} from "./components/sohc-eq-params-against-time/sohc-eq-params-against-time.component";
import {ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent} from "./components/charge-profile-fast-current-against-soc-for-each-temperature/charge-profile-fast-current-against-soc-for-each-temperature.component";
import {ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent} from "./components/charge-profile-protected-current-against-soc-for-each-temperture/charge-profile-protected-current-against-soc-for-each-temperture.component";
import {ResetPasswordComponent} from "./components/reset-password/reset-password.component";
import {CreateAccountComponent} from "./components/create-account/create-account.component";
import {VerifyAccountComponent} from "./components/verify-account/verify-account.component";
import {ResetPasswordPageComponent} from "./components/reset-password-page/reset-password-page.component";
import {ProfileComponent} from "./components/profile/profile.component";

const routes: Routes = [
  { path: '', component: AppComponent },
  { path: 'login', component: LoginComponent },
  { path: 'resetPassword', component: ResetPasswordComponent},
  { path: 'createAccount', component: CreateAccountComponent},
  { path: 'verify', component: VerifyAccountComponent},
  { path: 'reset', component: ResetPasswordPageComponent},
  {path:'profile', component:ProfileComponent},
  { path: 'homepage', component: HomepageComponent,
    children: [{path:PLOT_TYPES.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME, component:Sohc_min_cell_evolution_against_timeComponent},
      {path:PLOT_TYPES.SOHC_3_WORST_CELL_INDEX_AGAINST_TIME, component:Sohc_3_worst_cell_index_against_timeComponent},
      {path:PLOT_TYPES.SOHC_3_WORST_SOHCS_AGAINST_TIME, component:Sohc_3_worst_sohcs_against_timeComponent},
      {path:PLOT_TYPES.SOHC_EQ_PARAMS_AGAINST_TIME, component:SohcEqParamsAgainstTimeComponent},
      {path:PLOT_TYPES.CHARGE_PROFILE_FAST_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE, component:ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent},
      {path:PLOT_TYPES.CHARGE_PROFILE_PROTECTED_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE, component:ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
