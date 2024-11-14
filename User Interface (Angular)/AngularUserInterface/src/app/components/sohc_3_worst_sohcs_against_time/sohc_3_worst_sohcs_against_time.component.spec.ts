import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Sohc_3_worst_sohcs_against_timeComponent } from './sohc_3_worst_sohcs_against_time.component';

describe('ThirdtabComponent', () => {
  let component: Sohc_3_worst_sohcs_against_timeComponent;
  let fixture: ComponentFixture<Sohc_3_worst_sohcs_against_timeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [Sohc_3_worst_sohcs_against_timeComponent]
    });
    fixture = TestBed.createComponent(Sohc_3_worst_sohcs_against_timeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
