import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NZ_I18N } from 'ng-zorro-antd/i18n';
import { fr_FR } from 'ng-zorro-antd/i18n';
import { registerLocaleData } from '@angular/common';
import fr from '@angular/common/locales/fr';
import { ParametersComponent } from './parameters/parameters.component';
import { PreferencesComponent } from './preferences/preferences.component';
import { ManageMenuComponent } from './manage-menu/manage-menu.component';
import { ManageUserComponent } from './manage-user/manage-user.component';
import { ManageProductComponent } from './manage-product/manage-product.component';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzLayoutModule  } from 'ng-zorro-antd/layout';
import { IconsProviderModule } from './icons-provider.module';
import { DailyOrderComponent } from './daily-order/daily-order.component';
import { HistoricComponent } from './historic/historic.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';


registerLocaleData(fr);

@NgModule({
  declarations: [
    AppComponent,
    ParametersComponent,
    PreferencesComponent,
    ManageMenuComponent,
    ManageUserComponent,
    ManageProductComponent,
    DailyOrderComponent,
    HistoricComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    NzMenuModule,
    NzLayoutModule,
    IconsProviderModule,
    NgbModule 
  ],
  providers: [{ provide: NZ_I18N, useValue: fr_FR }],
  bootstrap: [AppComponent]
})
export class AppModule { }
