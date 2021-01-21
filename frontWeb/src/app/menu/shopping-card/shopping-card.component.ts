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

  confirmModal?: NzModalRef;

  constructor(private modal: NzModalService, private dailyOrderService: DailyOrderService) {}

  ngOnInit(): void {}

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
    if (this.products.length === 0) {
      this.confirmModal = this.modal.confirm({
        nzTitle: 'Votre panier est vide !',
        nzOnOk: () =>
          console.log("Panier vide !")
      });
    } else {
      if (this.userWallet < this.toPay) {
        this.confirmModal = this.modal.confirm({
          nzTitle: 'Votre solde est insuffisant !',
          nzOnOk: () =>
            console.log("Solde insuffisant !")
        });
      } else {
        this.dailyOrderService.addOrder(this.generateOrder())
          .subscribe(res => {
            console.log(res)
            prompt("Commande passée avec succès !")
          });
      }
    }
  }

  generateOrder(): any {
    let userId = this.userId;
    let constraintId = 5;
    let quantity: any = []

    for (let i = 0; i < this.products.length; i++) {

      let item = {
        quantity: this.products[i].quantity,
        mealId: this.products[i].meal.id,
        menuId: this.products[i].menuId
      }

      quantity.push(item);
    }

    let order: Order = {
      userId: userId,
      constraintId: constraintId,
      quantity: quantity
    };

    return order;
  }
}
