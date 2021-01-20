import { TestBed } from '@angular/core/testing';

import { DailyMenuService } from './authentication.service';

describe('DailyMenuService', () => {
  let service: DailyMenuService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DailyMenuService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
