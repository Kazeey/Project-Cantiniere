import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { AuthenticationComponent } from './authentication.component';

import { AuthenticationPageRoutingModule } from './authentication-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    AuthenticationPageRoutingModule
  ],
  declarations: [AuthenticationComponent]
})
export class AuthenticationPageModule {}
