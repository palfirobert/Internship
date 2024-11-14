import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {CustomValidators} from "../../resources/CustomValidators";
import {ResetPasswordService} from "../../services/reset-password.service";
import {statusTranslations} from "../../constants/constants";


@Component({
  selector: 'app-reset-password-page',
  templateUrl: './reset-password-page.component.html',
  styleUrls: ['./reset-password-page.component.css']
})
export class ResetPasswordPageComponent {
  passwordReset = false;
  showError = false;
  errorMessage: any;
  resetPasswordForm : FormGroup;

  private token: string | null;

  constructor(private router: Router,  private formBuilder: FormBuilder, private resetPasswordService: ResetPasswordService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });

    if(this.token == null || this.token==localStorage.getItem('accessToken')){
      this.router.navigate(['/login']).then(() => {});
    }

    this.showError = false;
    this.passwordReset = false;

    this.resetPasswordForm = this.formBuilder.group({
      password: [null, Validators.compose([
        Validators.required,
        Validators.minLength(7),
        CustomValidators.patternValidator(/\d/, { hasNumber: true }),
        // 3. check whether the entered password has upper case letter
        CustomValidators.patternValidator(/[A-Z]/, { hasCapitalCase: true }),
        // 4. check whether the entered password has a lower-case letter
        CustomValidators.patternValidator(/[a-z]/, { hasSmallCase: true }),
        CustomValidators.patternValidator(/[!@%&?]/, { hasSpecialCharacters: true }),
        CustomValidators.patternValidator(/^[^*$^(){}\[\]:;<>,./~_+\-=|]+$/, { hasOtherThanAllowed: true }),
        CustomValidators.patternValidator(/^[^\s]+$/, { hasWhiteSpaces: true })
      ])],
      confirmPassword: [null, [Validators.required]]
    },{
      validators: CustomValidators.passwordsMatch
    })
  }

  onSubmit() {
    console.log(this.showError);
    console.log(this.passwordReset);
    if(this.resetPasswordForm.invalid) {
      this.allRequiredCompleted();
    }
    else{
      this.resetPasswordService.resetPassword(this.token, this.resetPasswordForm.get("password")?.value).subscribe(
        (response) =>{
          this.showError = false;
          this.passwordReset = true;
        },
        (error => {
          this.showError = true;
          console.log(error);
          if(error.status==202) {
            this.errorMessage = error.error.text;
          } else {
            this.errorMessage = statusTranslations[error.status] || 'An error occurred';
          }
        })
      );
    }

  }

  allRequiredCompleted(){
    this.showError = !!(this.resetPasswordForm.get('password')?.hasError('required') ||
      this.resetPasswordForm.get('confirmPassword')?.hasError('required'));
  }
}
