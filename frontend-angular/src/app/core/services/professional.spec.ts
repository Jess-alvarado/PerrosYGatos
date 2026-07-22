import { TestBed } from '@angular/core/testing';

import { Professional } from './professional';

describe('Professional', () => {
  let service: Professional;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Professional);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
