import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DailyMenuManagerComponent } from './daily-menu-manager/daily-menu-manager.component';
import { ShoppingCartComponent } from './shopping-cart/shopping-cart.component';
import { MealComponent } from './meal/meal.component';
import { DrinkComponent } from './drink/drink.component';



@NgModule({
  declarations: [DailyMenuManagerComponent, ShoppingCartComponent, MealComponent, DrinkComponent],
  imports: [
    CommonModule
  ],
  exports: [DailyMenuManagerComponent]
})
export class DailyMenuModule { }
