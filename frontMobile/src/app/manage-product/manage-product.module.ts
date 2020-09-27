import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { ManageProductComponent } from './manage-product.component';

import { ManageProductPageRoutingModule } from './manage-product-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ManageProductPageRoutingModule
  ],
  declarations: [ManageProductComponent]
})
export class ManageProductPageModule {}
