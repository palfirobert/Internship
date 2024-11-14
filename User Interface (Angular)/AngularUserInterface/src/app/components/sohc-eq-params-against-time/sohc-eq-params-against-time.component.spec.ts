import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SohcEqParamsAgainstTimeComponent } from './sohc-eq-params-against-time.component';

describe('SohcEqParamsAgainstTimeComponent', () => {
  let component: SohcEqParamsAgainstTimeComponent;
  let fixture: ComponentFixture<SohcEqParamsAgainstTimeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SohcEqParamsAgainstTimeComponent]
    });
    fixture = TestBed.createComponent(SohcEqParamsAgainstTimeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
