import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {ResetPasswordService} from "../../services/reset-password.service";


@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {
  emailSent = false;
  email = '';
  errorMessage: any;
  resetFailed = false;

  constructor(private router: Router, private resetPasswordService: ResetPasswordService) {
  }

  ngOnInit(): void {
    this.emailSent = false;
  }

  onSubmit() {
    if(this.email == ''){
      this.resetFailed = true;
      this.errorMessage = 'Required Fields *';
    }
    else{
      console.log(this.resetPasswordService);
      this.resetPasswordService.sendEmailResetPassword(this.email).subscribe({
        error: error => {
          this.errorMessage = error.error.text;
          this.resetFailed = true;
        },
        complete: () => {
          this.emailSent = true;
        }
      });
    }
  }

  cancelResetPassword() {
    this.router.navigate(['/login']).then(() => {});
  }
}
