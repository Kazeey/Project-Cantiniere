import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { ManageMenuComponent } from './manage-menu.component';

import { ManageMenuPageRoutingModule } from './manage-menu-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ManageMenuPageRoutingModule
  ],
  declarations: [ManageMenuComponent]
})
export class ManageMenuPageModule {}
