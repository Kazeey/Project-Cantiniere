import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DailyMenuComponent } from './daily-menu.component';

const routes: Routes = [
  {
    path: '',
    component: DailyMenuComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class dailyMenuPageRoutingModule {}
