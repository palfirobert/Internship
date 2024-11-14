import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent } from './charge-profile-fast-current-against-soc-for-each-temperature.component';

describe('ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent', () => {
  let component: ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent;
  let fixture: ComponentFixture<ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent]
    });
    fixture = TestBed.createComponent(ChargeProfileFastCurrentAgainstSocForEachTemperatureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
