import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { MealService } from '../../services/meal/meal.service';

@Component({
  selector: 'app-meal-card',
  templateUrl: './meal-card.component.html',
  styleUrls: ['./meal-card.component.scss']
})
export class MealCardComponent implements OnInit {
  @Input() meal: any;
  @Input() isAuthenticate: boolean;

  image: any = {
    image64 : "https://lunawood.com/wp-content/uploads/2018/02/placeholder-image.png"
  }

  @Output() onQuantityChange = new EventEmitter<{meal: number, menuId: number, quantity: number}>();

  quantity: number = 0;

  placeholder: String = "Aucune description disponible pour ce plat."

  constructor(private mealService: MealService) { }

  ngOnInit(): void {
    this.getMealImage()
  }

  getMealImage() {
    this.mealService.getImage(this.meal.id)
      .subscribe(res => {
        this.image = res;
      })
  }

  onMinusBtn() {
    if (this.quantity > 0) {
      this.quantity--;

      this.onQuantityChange.emit({meal: this.meal, menuId: 0, quantity: this.quantity});
    }
  }

  onPlusBtn() {
    this.quantity++;

    this.onQuantityChange.emit({meal: this.meal, menuId: 0, quantity: this.quantity});
  }

}
