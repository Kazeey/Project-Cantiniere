import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { MealService } from '../services/meal/meal.service';
import { ManageMenuService } from '../services/manage-menu/manage-menu.service';
import { constantes } from '../../../../config/constantes';

@Component({
  selector: 'app-manage-product',
  templateUrl: './manage-product.component.html',
  styleUrls: ['./manage-product.component.scss']
})
export class ManageProductComponent implements OnInit {

  meals: any = [];
  meal: any;
  menus: any = [];

  // Use with Modal
  isVisible = false;
  editMode = false;
  modalTitleStr: string = "Créer un plat";

  // Menu field
  label: string;
  description: string;
  priceDF: number;
  availableWeek: any = [];
  week: string;

  // Search
  tempMeals: any;

  constructor(private mealService: MealService, private manageMenuService: ManageMenuService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  ngOnInit(): void 
  {
    this.isConnected = verification();

    this.getAllMeals();
    this.getAllMenus();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  getAllMeals() {
    this.mealService.getAllMeal()
      .subscribe(res => {
        this.meals = res;

        this.tempMeals = res

        console.log(res);
      })
  }

  getAllMenus() {
    this.manageMenuService.getAllMenus()
      .subscribe(res => {
        this.menus = res;

        console.log(res);
      })
  }

  onDeleteMeal(meal: any) {
      this.removeMealInMenus(meal);
  }

  removeMealInMenus(meal: any) {
    /*for (let menu of this.menus) {

      if (menu.meals !== undefined) {

        for (let i = 0; i < menu.meals.length; i++) {

          if (meal.id === menu.meals[i].id) {
            menu.meals.splice(i, 1)

            this.manageMenuService.updateMenu(menu.id, menu)
              .subscribe(res => {
                console.log(res)
              })
          }
        }
      }
    }*/
  }

  onAddMeal() {
    this.editMode = false;

    this.showMenuModal("Créer un menu");
  }

  onEditMeal(meal: any) {
    this.label = meal.label;
    this.description = meal.description;
    this.priceDF = meal.priceDF;
    this.availableWeek = meal.availableForWeeks;

    this.editMode = true;

    this.meal = meal;

    this.showMenuModal("Modifier le plat");
  }

  /* Show & Manage Modal For Menu */

  showMenuModal(title: string): void {
    this.modalTitleStr = title;

    this.isVisible = true;
  }

  handleMealOk(): void {
    if (this.editMode) {
      this.meal.label = this.label;
      this.meal.description = this.description;
      this.meal.priceDF = this.priceDF;
      this.meal.availableForWeeks = this.availableWeek;

      this.mealService.updateMeal(this.meal.id, this.meal)
      .subscribe(res => {
        console.log(res)
  
        this.updateViews()
      })
    } 
    else {
      this.meal = {
        label: this.label,
        description: this.description,
        priceDF: this.priceDF,
        status: 0,
        imageId: 34,
        availableForWeeks: this.availableWeek,
        ingredients: [2, 4, 8]
      }

      this.mealService.addMeal(this.meal)
      .subscribe(res => {
        console.log(res)

        this.updateViews()
      })
    }
    this.isVisible = false;

    this.resetField();
  }

  handleMealCancel(): void {
    this.isVisible = false;

    this.resetField();
  }

  onAddAvailableWeek() {
    this.availableWeek.push(this.week);

    this.week = "";
  }

  getAvailableWeek() {
    let str = "";

    for (let week of this.availableWeek) {
      str = str + week + " ";
    }

    return str;
  }

  resetField() {
    this.meal = null;
    this.label = null;
    this.priceDF = null;
    this.availableWeek = [];
  }

  updateViews() {
    this.menus = [];

    this.getAllMeals();
  }

  searchInMeals(value) {
    this.meals = []

    if (value.length > 0) {
      for (let i = 0; i < this.tempMeals.length; i++) {
        if (this.tempMeals[i].label.toLowerCase().includes(value.toLowerCase())) {
          this.meals.push(this.tempMeals[i])
        }
      }
    } else {
      this.meals = this.tempMeals
    }
  }
}
