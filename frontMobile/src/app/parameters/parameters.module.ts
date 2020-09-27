import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { ParametersComponent } from './parameters.component';

import { ParametersPageRoutingModule } from './parameters-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ParametersPageRoutingModule
  ],
  declarations: [ParametersComponent]
})
export class ParametersPageModule {}
