import { Component, OnInit } from '@angular/core';
import { MealService } from '../../services/meal/meal.service';

@Component({
  selector: 'app-daily-menu',
  templateUrl: './daily-menu.component.html',
  styleUrls: ['./daily-menu.component.scss'],
  providers : [
    MealService
  ]
})
export class DailyMenuComponent implements OnInit {
  productsToDeliver: any = [];

  isLunchLady: boolean = false;
  isAuthenticate: boolean = true
  userWallet: number = 75.15;
  userId = 1;

  meals: any = [];
  //menus: any = [];
  products: any = [];

  constructor(private mealService: MealService) { }

  ngOnInit(): void {
    //this.getDailyMenus(1);

    this.getMealForToday();
  }

  getMealForToday() {
    this.mealService.getMealsForToday()
      .subscribe(res => {
        console.log(res)

        this.meals = res;
      })
  }

  /*getDailyMenus(weekNumber: number) {
    this.dailyMenuService.getDailyMenu(weekNumber)
      .subscribe(res => {
        this.menus = res;
      })
  }*/

  onMeal(event: any) {
    let isContain: boolean = false;
    let index: number;
    
    if (this.products.length > 0) {
      for (let i = 0; i < this.products.length; i++) {
        if (this.products[i].meal === event.meal) {
          isContain = true;
          index = i;
        }
      }
    }

    if (isContain) {
      if(event.quantity === 0 ) this.products.splice(index, 1);
      else this.products[index] = event;
    }
    else this.products.push(event);
  }

}
