import { TestBed } from '@angular/core/testing';

import { DailyOrderService } from './daily-order.service';

describe('DailyOrderServicesService', () => {
  let service: DailyOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DailyOrderService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
