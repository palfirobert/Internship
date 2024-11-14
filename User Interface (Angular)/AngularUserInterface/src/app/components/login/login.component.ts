import {Component, OnInit} from '@angular/core';
import {LoginService} from "../../services/login.service";
import {BatteryUser} from "../../models/BatteryUser";
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import {statusTranslations} from  "../../constants/constants";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit{
  title = 'Login';
  loginFailed = false;
  errorMessage = '';
  user: BatteryUser = { id: 1, email: '', password: '' };

  constructor(private loginService: LoginService, private router: Router, private titleService: Title) {}

  ngOnInit(): void {
    let token;
    token = localStorage.getItem('accessToken');
    if (token !== null) {
      this.router.navigate(['/homepage']).then(() => {});
    } else {
      this.titleService.setTitle('Login');
    }
  }

  onSubmit() {
    if (this.user.email == '' || this.user.password == '') {
      this.loginFailed = true;
      this.errorMessage = 'Required Fields *';
    } else {

      this.loginService.login(this.user.email, this.user.password).subscribe({
        error: error => {
          this.loginFailed = true;
          this.errorMessage = statusTranslations[error.status] || 'An error occurred';
        },
        complete: () => {
          localStorage.setItem('email',this.user.email);
          localStorage.setItem('password',this.user.password);
          this.router.navigate(['/homepage']).then(() => {});
        }
      });
    }
  }
}
