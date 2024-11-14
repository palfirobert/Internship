import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent } from './charge-profile-protected-current-against-soc-for-each-temperture.component';

describe('ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent', () => {
  let component: ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent;
  let fixture: ComponentFixture<ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent]
    });
    fixture = TestBed.createComponent(ChargeProfileProtectedCurrentAgainstSocForEachTempertureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
