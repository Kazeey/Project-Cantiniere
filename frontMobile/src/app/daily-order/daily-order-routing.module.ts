import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DailyOrderComponent } from './daily-order.component';

const routes: Routes = [
  {
    path: '',
    component: DailyOrderComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class dailyOrderPageRoutingModule {}
