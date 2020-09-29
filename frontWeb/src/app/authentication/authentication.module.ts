import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthenticationComponent } from './authentication.component';

import { AuthenticationPageRoutingModule } from './authentication-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    AuthenticationPageRoutingModule
  ],
  declarations: [AuthenticationComponent]
})
export class AuthenticationPageModule {}
