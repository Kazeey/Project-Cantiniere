import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class MealService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode + "meals/"; // url souhaitée pour la requête de l'API

  addMeal(meal: any): Observable<Object> {
    return this.http.post(this.url + "addMeal/", {meal: meal});
  }

  updateMeal(mealId: number, meal: any): Observable<Object> {
    return this.http.post(this.url + "updateMeal/", {mealId: mealId, meal: meal});
  }

  getAllMeal(): Observable<Object> {
    return this.http.get(this.url + "getAllMeals/");
  }

  getMealsByWeekNb(weekNb: number): Observable<Object> {
    return this.http.post(this.url + "getMealsByWeekNb/", {weekNb: weekNb});
  }

  getMealsForToday(): Observable<Object> {
    return this.http.post(this.url + "getMealsForToday/", {});
  }

  deleteMeal(mealId): Observable<Object> {
    return this.http.post(this.url + "deleteMeal/", {mealId: mealId})
  }

  getImage(mealId: number): Observable<Object> {
    return this.http.post(this.url + "getImage/", {id: mealId});
  }
}
