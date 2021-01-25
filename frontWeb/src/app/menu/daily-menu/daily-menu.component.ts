import { Component, OnInit, Input } from '@angular/core';
import { verification } from '../../../../../config/verification';
import { MealService } from '../../services/meal/meal.service';
import { ManageUserService } from '../../services/manage-user/manage-user.service';

@Component({
  selector: 'app-daily-menu',
  templateUrl: './daily-menu.component.html',
  styleUrls: ['./daily-menu.component.scss'],
  providers : [
    MealService
  ]
})
export class DailyMenuComponent implements OnInit {
  userId: number;
  userWallet: number;
  isLunchLady: boolean;

  isAuthenticate: boolean;

  isCartVisible: boolean = false;

  meals: any = [];
  products: any = [];
  orderProducts: any = [];

  constructor(private mealService: MealService, private manageUserService: ManageUserService) { }

  ngOnInit(): void {
    this.initComponent();
  }

  initComponent() {
    this.userId = parseInt(localStorage.getItem("userId"));
    this.isAuthenticate = localStorage.getItem("connected") === "true" ? true : false;

    this.getUserById(this.userId);
  }

  getUserById(userId: number) {
    this.manageUserService.getUserById(userId)
      .subscribe(res => {
        let user: any = res[0];
        console.log(user)

        this.isLunchLady = user.role === "Client" ? false : true;
        this.userWallet = user.wallet;

        this.updateView();

        this.getMealForToday();
      });
  }

  getMealForToday() {
    this.mealService.getMealsForToday()
      .subscribe(res => {
        this.meals = res;

        for (let meal of this.meals) {
          this.products.push({meal: meal, menuId: 0, quantity: 0})
        }
      })
  }

  onAdd(event: any) {
    let isContain: boolean = false;
    let index: number;
    
    if (this.orderProducts.length > 0) {
      for (let i = 0; i < this.orderProducts.length; i++) {
        if (this.orderProducts[i].meal === event.meal) {
          isContain = true;
          index = i;
        }
      }
    }

    if (isContain) {
      if(event.quantity === 0 ) this.orderProducts.splice(index, 1);
      else this.orderProducts[index] = event;
    }
    else this.orderProducts.push(event);

    console.log(this.orderProducts)
  }

  updateView() {
    this.displayCart();
  }

  displayCart() {
    if (this.isAuthenticate) {
      if (this.isLunchLady === false) {
        this.isCartVisible = true
      }
    }
  }
}
