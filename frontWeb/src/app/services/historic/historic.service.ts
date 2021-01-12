import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AccueilService } from '../../../app/services/accueil.service';
import { Orders } from '../../../app/interfaces/order';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class HistoricService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode + "order/"; // url souhaitée pour la requête de l'API

  getOrderByUser(paramsOrder: Orders){
    return this.http.get(this.url + "getOrderByUser/"); // Récupère les données renvoyées par l'API
  }
}
