import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { AuthenticationComponent } from './authentication.component';
import { HttpClientModule } from '@angular/common/http';

import { AuthenticationPageRoutingModule } from './authentication-routing.module';


@NgModule({
  imports: [
    HttpClientModule,
    CommonModule,
    FormsModule,
    IonicModule,
    AuthenticationPageRoutingModule
  ],
  declarations: [AuthenticationComponent]
})
export class AuthenticationPageModule {}
