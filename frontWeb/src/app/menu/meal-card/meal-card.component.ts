import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { MealService } from '../../services/meal/meal.service';

@Component({
  selector: 'app-meal-card',
  templateUrl: './meal-card.component.html',
  styleUrls: ['./meal-card.component.scss']
})
export class MealCardComponent implements OnInit {
  @Input() product: any;
  @Input() isAuthenticate: boolean;
  @Input() isLunchLady: boolean;

  image: any = {
    image64 : "https://lunawood.com/wp-content/uploads/2018/02/placeholder-image.png"
  }

  @Output() onAddMenu = new EventEmitter<any>();

  placeholder: String = "Aucune description disponible pour ce plat."

  constructor(private mealService: MealService) { }

  ngOnInit(): void {
    this.getMealImage()
  }

  getMealImage() {
    this.mealService.getImage(this.product.meal.id)
      .subscribe(res => {
        this.image = res;
      })
  }

  canAdd(): boolean {
    return this.isLunchLady === false && this.isAuthenticate && this.product.quantity === 0 ? true : false;
  }

  onAdd() {
    this.product.quantity = 1;

    this.onAddMenu.emit(this.product);
  }

  /*onMinusBtn() {
    if (this.quantity > 0) {
      this.quantity--;

      this.onQuantityChange.emit({meal: this.meal, menuId: 0, quantity: this.quantity});
    }
  }

  onPlusBtn() {
    this.quantity++;

    this.onQuantityChange.emit({meal: this.meal, menuId: 0, quantity: this.quantity});
  }*/

}
