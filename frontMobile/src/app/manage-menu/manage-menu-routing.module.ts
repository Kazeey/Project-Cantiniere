import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManageMenuComponent } from './manage-menu.component';

const routes: Routes = [
  {
    path: '',
    component: ManageMenuComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManageMenuPageRoutingModule {}
