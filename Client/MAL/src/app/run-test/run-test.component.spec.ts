import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RunTestComponent } from './run-test.component';

describe('RunTestComponent', () => {
  let component: RunTestComponent;
  let fixture: ComponentFixture<RunTestComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RunTestComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RunTestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
