import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { DailyOrderComponent } from './daily-order.component';

import { dailyOrderPageRoutingModule } from './daily-order-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    dailyOrderPageRoutingModule
  ],
  declarations: [DailyOrderComponent]
})
export class dailyOrderPageModule {}
