import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class DailyMenuService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode + "login/forgotPassword"; // url souhaitée pour la requête de l'API
  
  getDailyMenu(): Observable<Object>
  {
    return this.http.get(this.url); // Récupère les données renvoyées par l'API
  }
}
