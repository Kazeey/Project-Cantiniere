import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthenticationComponent } from './authentication/authentication.component';
import { DailyOrderComponent } from './daily-order/daily-order.component';
import { HistoricComponent } from './historic/historic.component';
import { ManageMenuComponent } from './manage-menu/manage-menu.component';
import { ManageProductComponent } from './manage-product/manage-product.component';
import { ManageUserComponent } from './manage-user/manage-user.component';
import { ParametersComponent } from './parameters/parameters.component';
import { PreferencesComponent } from './preferences/preferences.component';

// Setup routing, identification de chaque routes, avec chaque url
const routes: Routes = [
  {
    path: 'authentication',
    component : AuthenticationComponent,
    data : {test : "Vient de routes"}
  },
  {
    path: 'historic',
    component : HistoricComponent
  },
  {
    path: 'preferences',
    component : PreferencesComponent
  },
  {
    path: 'parameters',
    component : ParametersComponent
  },
  {
    path: 'dailyOrder',
    component : DailyOrderComponent
  },
  {
    path: 'manageMenu',
    component : ManageMenuComponent
  },
  {
    path: 'manageUser',
    component : ManageUserComponent
  },
  {
    path: 'manageProduct',
    component : ManageProductComponent
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
