import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Sohc_3_worst_cell_index_against_timeComponent } from './sohc_3_worst_cell_index_against_time.component';

describe('SecondtabComponent', () => {
  let component: Sohc_3_worst_cell_index_against_timeComponent;
  let fixture: ComponentFixture<Sohc_3_worst_cell_index_against_timeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [Sohc_3_worst_cell_index_against_timeComponent]
    });
    fixture = TestBed.createComponent(Sohc_3_worst_cell_index_against_timeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
