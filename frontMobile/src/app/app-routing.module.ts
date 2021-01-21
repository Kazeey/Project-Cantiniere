import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authentication',
    loadChildren: () => import('./authentication/authentication.module').then( m => m.AuthenticationPageModule)
  },
  {
    path: 'preferences',
    loadChildren: () => import('./preferences/preferences.module').then( m => m.PreferencesPageModule)
  },
  {
    path: 'historic',
    loadChildren: () => import('./historic/historic.module').then( m => m.historicPageModule)
  },
  {
    path: 'dailyOrder',
    loadChildren: () => import('./daily-order/daily-order.module').then( m => m.dailyOrderPageModule)
  },
  {
    path: 'dailyMenu',
    loadChildren: () => import('./daily-menu/daily-menu.module').then( m => m.dailyMenuPageModule)
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
    redirectTo: 'dailyMenu',
    pathMatch: 'full'
  }

];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }

