import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyMenuManagerComponent } from './daily-menu-manager.component';

describe('DailyMenuManagerComponent', () => {
  let component: DailyMenuManagerComponent;
  let fixture: ComponentFixture<DailyMenuManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DailyMenuManagerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DailyMenuManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
