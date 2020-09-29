import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PreferencesComponent } from './preferences.component';

import { PreferencesPageRoutingModule } from './preferences-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    PreferencesPageRoutingModule
  ],
  declarations: [PreferencesComponent]
})
export class PreferencesPageModule {}
