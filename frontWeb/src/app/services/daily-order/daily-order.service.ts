import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class DailyOrderService {
  
  constructor(private http:HttpClient) { }

  private dailyOrderUrl = constantes.urlAPINode + "login/forgotPassword"; // url souhaitée pour la requête de l'API

  getDailyOrder(): Observable<Object>
  {
    return this.http.get(this.dailyOrderUrl); // Récupère les données renvoyées par l'API
  }
}
