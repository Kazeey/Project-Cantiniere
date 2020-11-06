import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { DailyMenuComponent } from './daily-menu.component';

import { dailyMenuPageRoutingModule } from './daily-menu-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    dailyMenuPageRoutingModule
  ],
  declarations: [DailyMenuComponent]
})
export class dailyMenuPageModule {}
