import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ParametersComponent } from './parameters.component';

import { ParametersPageRoutingModule } from './parameters-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ParametersPageRoutingModule
  ],
  declarations: [ParametersComponent]
})
export class ParametersPageModule {}
