import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManageProductComponent } from './manage-product.component';

import { ManageProductPageRoutingModule } from './manage-product-routing.module';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ManageProductPageRoutingModule
  ],
  declarations: [ManageProductComponent]
})
export class ManageProductPageModule {}
