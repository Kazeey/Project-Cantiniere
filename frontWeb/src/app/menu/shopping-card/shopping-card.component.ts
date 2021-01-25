import { Component, OnInit, Input } from '@angular/core';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { Order } from '../../interfaces/order'
import { DailyOrderService } from '../../services/daily-order/daily-order.service';

@Component({
  selector: 'app-shopping-card',
  templateUrl: './shopping-card.component.html',
  styleUrls: ['./shopping-card.component.scss'],
  providers : [
    DailyOrderService
  ]
})
export class ShoppingCardComponent implements OnInit {
  @Input() products: any;
  @Input() userWallet: number;
  @Input() userId: number;

  toPay: number;

  order: Order;

  confirmModal?: NzModalRef;

  constructor(private modal: NzModalService, private dailyOrderService: DailyOrderService) {}

  ngOnInit(): void {}

  onMinusBtn(product: any, index: number) {
    if (product.quantity > 0) product.quantity--;

    console.log(product)
  }

  onPlusBtn(product: any) {
    product.quantity++;

    console.log(product)
  }

  calculateLine(index: number) {
    let amount = this.products[index].meal.priceDF * this.products[index].quantity;
    
    return amount.toFixed(2);
  }

  calculateAmount() {
    let amount = 0;

    for (let i = 0; i < this.products.length; i++) {
      amount = amount + this.products[i].meal.priceDF * this.products[i].quantity;
    }

    this.toPay = amount;

    return amount.toFixed(2);
  }

  showConfirm(): void {
    if (this.userWallet < this.toPay) {
      this.confirmModal = this.modal.confirm({
        nzTitle: 'Votre solde est insuffisant !',
        nzOnOk: () =>
          console.log("Solde insuffisant !")
      });
    } else {
      this.generateOrder();

      if (this.order.quantity.length > 0) {
        console.log(this.order)
        this.dailyOrderService.addOrder(this.order)
          .subscribe(res => {
            console.log(res)
            alert("Commande passé avec succès")
          });
      } else {
        this.confirmModal = this.modal.confirm({
          nzTitle: 'Votre panier est vide !',
          nzOnOk: () =>
            console.log("Panier vide !")
        });
      }
    }
  }

  generateOrder() {
    let userId = this.userId;
    let constraintId = 5;
    let quantity: any = []

    for (let i = 0; i < this.products.length; i++) {

      if (this.products[i].quantity > 0) {
        let item = {
          quantity: this.products[i].quantity,
          mealId: this.products[i].meal.id,
          menuId: this.products[i].menuId
        }
  
        quantity.push(item);
      }
    }

    this.order = {
      userId: userId,
      constraintId: constraintId,
      quantity: quantity
    };
  }
}
