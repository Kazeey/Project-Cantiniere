import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManageMenuComponent } from './manage-menu.component';

import { ManageMenuPageRoutingModule } from './manage-menu-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ManageMenuPageRoutingModule
  ],
  declarations: [ManageMenuComponent]
})
export class ManageMenuPageModule {}
