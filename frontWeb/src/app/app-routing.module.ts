import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('./authentication/authentication.module').then( m => m.AuthenticationPageModule)
  },
  {
    path: 'preferences',
    loadChildren: () => import('./preferences/preferences.module').then( m => m.PreferencesPageModule)
  },
  {
    path: 'parameters',
    loadChildren: () => import('./parameters/parameters.module').then( m => m.ParametersPageModule)
  },
  {
    path: 'manageMenu',
    loadChildren: () => import('./manage-menu/manage-menu.module').then( m => m.ManageMenuPageModule)
  },
  {
    path: 'manageUser',
    loadChildren: () => import('./manage-user/manage-user.module').then( m => m.ManageUserPageModule)
  },
  {
    path: 'manageProduct',
    loadChildren: () => import('./manage-product/manage-product.module').then( m => m.ManageProductPageModule)
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
