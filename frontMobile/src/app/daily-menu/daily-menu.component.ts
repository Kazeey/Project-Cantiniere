import { Component, OnInit } from '@angular/core';
import { DailyMenuService } from '../services/daily-menu/daily-menu.service';
import { DailyOrderService } from '../services/daily-order/daily-order.service';
import { MealService } from '../services/meal/meal.service';

import { AlertController } from '@ionic/angular';

@Component({
  selector: 'app-daily-menu',
  templateUrl: './daily-menu.component.html',
  styleUrls: ['./daily-menu.component.scss'],
})
export class DailyMenuComponent implements OnInit {
  menus: any = [];
  meals: any = [];
  products: any = [];

  amountToPay: number;

  isAuthenticate: boolean = true;
  userWallet: number = 82.13;
  userId: number = 1;

  constructor(private dailyMenuService: DailyMenuService, 
    private dailyOrderService: DailyOrderService, 
    private mealService: MealService, 
    public alertController: AlertController) { }

  ngOnInit() {
    //this.getDailyMenu(3)

    this.getDailyMeals()
  }

  // Get all Meal for the current Week
  getDailyMeals() {
    this.mealService.getMealsForToday()
      .subscribe(res => {
        this.meals = res

        console.log(res);
      })
  }

  onAdd(meal: any) {
    this.products.push({meal: meal, menuId: 0, quantity: 1})

    console.log(this.products)
  }

  isAddedToCart(meal): boolean {
    let isAdded = false;

    for (let product of this.products) {
      if (product.meal === meal) {
        isAdded = true;
      }
    }

    return !isAdded;
  }

  onLess(meal: any) {
    if (this.products.length > 0) {
      for (let i = 0; i < this.products.length; i++) {
        if (this.products[i].meal === meal) {
          if (this.products[i].quantity > 0) {
            this.products[i].quantity--

            if (this.products[i].quantity === 0) this.products.splice(i, 1);
          } 
        }
      }
    }

    console.log(this.products);

  }

  onPlus(meal: any) {
    let isContain: boolean = false;
    let index: number;

    let newProduct = {meal: meal, quantity: 1};

    if (this.products.length > 0) {
      for (let i = 0; i < this.products.length; i++) {
        if (this.products[i].meal === meal) {
          isContain = true;
          index = i;
        } 
      }
    } 

    if (isContain) {
      this.products[index].quantity++;
    } else {
      this.products.push(newProduct);
    }

    console.log(this.products);
  }

  getProductPrice(product: any): String{
    let price: number = 0;

    price = product.meal.priceDF * product.quantity;

    return price.toFixed(2) + " €";
  }

  getCartPrice(): String {
    let price: number = 0;

    for (let product of this.products) {
      price = price + (product.meal.priceDF * product.quantity);
    }

    this.amountToPay = parseFloat(price.toFixed(2));

    return price.toFixed(2) + "€";
  }

  /* Order */

  onOrder() {
    let message: string = ""
    let positiveBtn: string = "Ok"

    if (this.amountToPay < this.userWallet) {
      message = "Voulez vous procéder à l'enregistrement de votre commande ?"
      positiveBtn = "Valider"
    } else {
      message = "Votre solde est insuffisant"
    }

    this.showAlert(message, positiveBtn);
  }

  async showAlert(message: string, positiveBtn: string) {
    const alert = await this.alertController.create({
      cssClass: 'my-custom-class',
      subHeader: message,
      buttons: [
        {
          text: positiveBtn,
          cssClass: 'primary',
          handler: (onPositive) => {
            let order = this.generateOrder();

            console.log(order);

            this.dailyOrderService.addOrder(order).subscribe(res => {
              console.log(res);
            })
          }
        }
      ]
    });

    await alert.present();
  }

  generateOrder(): any {
    let userId = this.userId;
    let constraintId = 1;
    let quantity: any = []

    for (let i = 0; i < this.products.length; i++) {

      let item = {
        quantity: this.products[i].quantity,
        mealId: this.products[i].meal.id,
        menuId: this.products[i].menuId
      }

      quantity.push(item);
    }

    let order: any = {
      userId: userId,
      constraintId: constraintId,
      quantity: quantity
    };

    return order;
  }

}
