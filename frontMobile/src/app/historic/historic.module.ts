import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { HistoricComponent } from './historic.component';

import { historicPageRoutingModule } from './historic-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    historicPageRoutingModule
  ],
  declarations: [HistoricComponent]
})
export class historicPageModule {}
