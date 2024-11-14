import { Component } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RegisterService} from "../../services/register.service";


@Component({
  selector: 'app-verify-account',
  templateUrl: './verify-account.component.html',
  styleUrls: ['./verify-account.component.css']
})
export class VerifyAccountComponent {
  message: any;
  private token: string | null;

  constructor(private registerService: RegisterService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });
    if (this.token != null) {
      this.registerService.verify(this.token).subscribe({
        error: error => {
          if(error.status==202) {
            this.message = error.error.text;
          } else{
            console.log(error.text);
            this.message = "Unknown error";
          }
        },
        complete: () => {
          this.message = "Your account was verified";
        }
      });
    }
  }
}
