import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class HistoricService {

  constructor(private http:HttpClient) { }

  private urlOrder = constantes.urlAPINode + "order/"; // url souhaitée pour la requête de l'API
  private urlUser = constantes.urlAPINode + "users/"; // url souhaitée pour la requête de l'API


  getOrderByUser(userId){
    return this.http.post(this.urlOrder + "getOrderByUser", {userId:userId}); // Récupère les données renvoyées par l'API
  }

  getUserById(userId){
    return this.http.post(this.urlUser + "getUserById", {userId:userId}); // Récupère les données renvoyées par l'API
    
  }
}
