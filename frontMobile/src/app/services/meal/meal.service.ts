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

  getMealsByWeekNb(weekNb: number): Observable<Object> {
    return this.http.post(this.url + "getMealsByWeekNb/", {weekNb: weekNb});
  }

  getMealsForToday(): Observable<Object> {
    return this.http.post(this.url + "getMealsForToday/", {});
  }

  getImage(mealId: number): Observable<Object> {
    return this.http.post(this.url + "getImage/", {id: mealId});
  }
}
