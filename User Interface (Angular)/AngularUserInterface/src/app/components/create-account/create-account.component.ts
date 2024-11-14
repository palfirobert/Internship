import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RegisterService} from "../../services/register.service";
import {statusTranslations} from "../../constants/constants";
import {CustomValidators} from "../../resources/CustomValidators";


@Component({
  selector: 'app-create-account',
  templateUrl: './create-account.component.html',
  styleUrls: ['./create-account.component.css']
})
export class CreateAccountComponent {
  showError = false;
  errorMessage = "Required fields *";
  accountCreated = false;

  createAccountForm : FormGroup;

  constructor(private router: Router,  private formBuilder: FormBuilder, private registerService: RegisterService) {
  }

  ngOnInit(): void {
    this.showError = false;
    this.accountCreated = false;

    this.createAccountForm = this.formBuilder.group({
      familyName: [null, [Validators.required]],
      givenName: [null, [Validators.required]],
      email: [null, Validators.compose([
        Validators.required,
        CustomValidators.patternValidator(/.+@bosch\.com$/, { invalidEmail: true })
      ])],
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


  cancelCreateAccount() {
    this.router.navigate(['/login']).then(() => {});
  }


  onSubmit() {
    if(this.createAccountForm.invalid) {
      this.allRequiredCompleted();
    }
    else{
      this.populateNewUserService()

      this.registerService.register().subscribe({
        next: data => {
          console.log(data);
        },
        error: error => {
          this.showError = true;
          if(error.status==202) {
            this.errorMessage = error.error.text;
          } else {
            this.errorMessage = statusTranslations[error.status] || 'An error occurred';
          }
        },
        complete: () => {
          this.showError = false;
          this.accountCreated = true;
        }
      });
    }
  }

  populateNewUserService(){
    this.registerService.newUser.familyName = this.createAccountForm.get('familyName')?.value;
    this.registerService.newUser.givenName = this.createAccountForm.get('givenName')?.value;
    this.registerService.newUser.email = this.createAccountForm.get('email')?.value;
    this.registerService.newUser.password = this.createAccountForm.get('password')?.value;
  }

  allRequiredCompleted(){
    this.showError = !!(this.createAccountForm.get('familyName')?.hasError('required') ||
      this.createAccountForm.get('givenName')?.hasError('required') ||
      this.createAccountForm.get('email')?.hasError('required') ||
      this.createAccountForm.get('password')?.hasError('required') ||
      this.createAccountForm.get('confirmPassword')?.hasError('required'));
  }
}
