import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShoppingCardComponent } from './shopping-card/shopping-card.component';
import { MenuComponent } from './menu/menu.component';
import { DailyMenuComponent } from './daily-menu/daily-menu.component';
import { MealCardComponent } from './meal-card/meal-card.component';

import { NZ_I18N } from 'ng-zorro-antd/i18n';
import { fr_FR } from 'ng-zorro-antd/i18n';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzLayoutModule  } from 'ng-zorro-antd/layout';
import { NzModalModule } from 'ng-zorro-antd/modal';

@NgModule({
  declarations: [ShoppingCardComponent, MenuComponent, DailyMenuComponent, MealCardComponent],
  imports: [
    CommonModule,
    NzButtonModule,
    NzMenuModule,
    NzLayoutModule,
    NzModalModule
  ],
  providers: [{ provide: NZ_I18N, useValue: fr_FR }],
  exports: [DailyMenuComponent]
})

export class MenuModule { }
