import { TestBed } from '@angular/core/testing';

import { TimeoutDialogService } from './timeout-dialog.service';

describe('TimeoutDialogService', () => {
  let service: TimeoutDialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TimeoutDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
