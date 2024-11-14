import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Sohc_min_cell_evolution_against_timeComponent } from './sohc_min_cell_evolution_against_time.component';

describe('FirsttabComponent', () => {
  let component: Sohc_min_cell_evolution_against_timeComponent;
  let fixture: ComponentFixture<Sohc_min_cell_evolution_against_timeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [Sohc_min_cell_evolution_against_timeComponent]
    });
    fixture = TestBed.createComponent(Sohc_min_cell_evolution_against_timeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
