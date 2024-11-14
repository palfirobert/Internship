import {Injectable, Component, Input, OnInit, ChangeDetectorRef} from '@angular/core';

import {NgbModal, NgbModalOptions, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';


@Component({
  template: `
    <div class="modal-header">
      <h4 class="modal-title">{{ title }}</h4>
      <button type="button" class="close" aria-label="Close" (click)="activeModal.dismiss('Cross click')">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="modal-body">
      <p [innerHTML]="message"></p>
    </div>
    <div class="modal-footer">
      <button *ngIf="showCancel" type="button" class="btn btn-secondary"
              (click)="activeModal.close(false)">{{ cancelText }}</button>
      <button type="button" class="btn btn-secondary" (click)="activeModal.close(true)">{{ confirmText }}</button>
    </div>
  `
})


export class TimeoutDialogComponent implements OnInit {
  @Input() title: string;
  @Input() message: string;
  @Input() showCancel = false;
  @Input() confirmText: string = 'Ok';
  @Input() cancelText: string = 'Cancel';


  constructor(public activeModal: NgbActiveModal, public changeRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
  }

}


@Injectable({
  providedIn: 'root'
})
export class TimeoutDialogService {


  constructor(private modalService: NgbModal) {
  }


  public confirm() {
    const modalRef = this.modalService.open(TimeoutDialogComponent);


    const instance = (modalRef as any)._windowCmptRef.instance;
    instance.windowClass = '';


    setTimeout(() => {
      instance.windowClass = 'custom-show';
    }, 0);


    const fx = (modalRef as any)._removeModalElements.bind(modalRef);
    (modalRef as any)._removeModalElements = () => {
      instance.windowClass = '';
      setTimeout(fx, 250);
    };


    modalRef.componentInstance.title = 'Discard Changes?';
    modalRef.componentInstance.message = 'Are you sure you want to discard your changes?';
    modalRef.componentInstance.changeRef.markForCheck();
    return modalRef.result;
  }


  public open(title: string, message: string, showCancel: boolean = false, confirmText: string = 'Ok', cancelText: string = 'Cancel',
              options: NgbModalOptions = {size: 'sm'}) {
    const modalRef = this.modalService.open(TimeoutDialogComponent, options);


    const instance = (modalRef as any)._windowCmptRef.instance;


    setTimeout(() => {
      instance.windowClass = 'custom-show';
    }, 0);


    const fx = (modalRef as any)._removeModalElements.bind(modalRef);
    (modalRef as any)._removeModalElements = () => {
      instance.windowClass = '';
      setTimeout(fx, 250);
    };


    modalRef.componentInstance.title = title;
    modalRef.componentInstance.message = message;
    modalRef.componentInstance.showCancel = showCancel;
    modalRef.componentInstance.confirmText = confirmText;
    modalRef.componentInstance.cancelText = cancelText;
    modalRef.componentInstance.changeRef.markForCheck();
    return modalRef.result;
  }
}
