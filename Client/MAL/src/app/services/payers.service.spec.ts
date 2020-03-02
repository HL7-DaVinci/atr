import { TestBed } from '@angular/core/testing';

import { PayersService } from './payers.service';

describe('PayersService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PayersService = TestBed.get(PayersService);
    expect(service).toBeTruthy();
  });
});
