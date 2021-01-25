import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Menu } from '../../interfaces/menu'

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class DailyMenuService {

  constructor(private http:HttpClient) {}

  private url = constantes.urlAPINode + "menu/"; // url souhaitée pour la requête de l'API
  
  updateMenu(menuId, menuToAdd): Observable<Object> {
    return this.http.post(this.url + "updateMenu/", {menuId: menuId, menu: menuToAdd});
  }

  getMenuById(menuId): Observable<Object> {
    return this.http.post(this.url + "getMenuById/", {menuId: menuId});
  }

  /*updateMenuImage(): Observable<Object> {
    return this.http.patch(this.url + "updateMenuImage/");
  }*/

  /*getMenuImage(): Observable<Object> {
    return this.http.get(this.url + "getMenuImage/");
  }*/
  
  getDailyMenu(weekNumber): Observable<Object> {
    return this.http.post(this.url + "getMenuForWeek/", {weeknumber: weekNumber}) // Récupère les données renvoyées par l'API
  }

 /* getMenuForToday(): Observable<Object> {
    return this.http.get(this.url + "getMenuForToday/");
  }*/
}
