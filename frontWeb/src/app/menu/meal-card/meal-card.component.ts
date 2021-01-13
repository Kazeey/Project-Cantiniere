import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';

@Component({
  selector: 'app-meal-card',
  templateUrl: './meal-card.component.html',
  styleUrls: ['./meal-card.component.scss']
})
export class MealCardComponent implements OnInit {
  @Input() meal: any;
  @Input() menuId: number;
  @Input() isAuthenticate: boolean;

  @Output() onQuantityChange = new EventEmitter<{meal: number, menuId: number, quantity: number}>();

  quantity: number = 0;
  mealAsProduct: any;

  placeholder: String = "Aucune description disponible pour ce plat."

  constructor() { }

  ngOnInit(): void { }

  onMinusBtn() {
    if (this.quantity > 0) {
      this.quantity--;

      this.onQuantityChange.emit({meal: this.meal, menuId: this.menuId, quantity: this.quantity});
    }
  }

  onPlusBtn() {
    this.quantity++;

    this.onQuantityChange.emit({meal: this.meal, menuId: this.menuId, quantity: this.quantity});
  }

}
