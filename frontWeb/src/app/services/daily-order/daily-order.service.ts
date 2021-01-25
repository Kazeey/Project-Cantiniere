import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class DailyOrderService {

  constructor(private http:HttpClient) 
  {

  }

  private url = constantes.urlAPINode + "order/"; // url souhaitée pour la requête de l'API

  addOrder(order: any): Observable<Object> 
  {
    return this.http.post(this.url + "addOrder/", {order: order});
  }

  getDailyOrder(myDate): Observable<Object> 
  {
    return this.http.get(this.url + "getDailyOrder?status=0&beginDate="+ myDate +"&endDate="+ myDate +""); // Récupère les données renvoyées par l'API
  }

  confirmOrder(orderId, myDate, verifNotif, userId)
  {
    this.http.post(this.url + "confirmOrder", {orderId : orderId, verifNotif : verifNotif, userId : userId}).subscribe();

    return this.getDailyOrder(myDate);
  }
}
