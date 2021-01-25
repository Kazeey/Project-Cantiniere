import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class ManageMenuService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode + "menu/"; // url souhaitée pour la requête de l'API

  getAllMenus(): Observable<Object> {
    return this.http.get(this.url + "getAllMenus/");
  }

  addMenu(menuToAdd: any): Observable<Object> {
    return this.http.post(this.url + "addMenu/", {menu: menuToAdd});
  }

  updateMenu(menuId, menuToAdd): Observable<Object> {
    return this.http.post(this.url + "updateMenu/", {menuId: menuId, menu: menuToAdd});
  }
  
  deleteMenu(menuId): Observable<Object> {
    return this.http.post(this.url + "deleteMenu/", {menuId: menuId})
  }
}
