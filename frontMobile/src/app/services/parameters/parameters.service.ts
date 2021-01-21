import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class ParametersService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode + "constraints"; // url souhaitée pour la requête de l'API

  getAllConstraints()
  {
    return this.http.get(this.url + "/getAllConstraints");
  }

  addConstraint(orderTimeLimit, maximumOrderPerDay, rateVAT)
  {
    this.http.post(this.url + "/addConstraint", {orderTimeLimit : orderTimeLimit, maximumOrderPerDay : maximumOrderPerDay, rateVAT : rateVAT})
    .subscribe(res => {});

    return this.getAllConstraints();
  }

  editConstraint(constraintId, orderTimeLimit, maximumOrderPerDay, rateVAT)
  {
    this.http.post(this.url + "/updateConstraint", {
      constraintId : constraintId,
      orderTimeLimit : orderTimeLimit,
      maximumOrderPerDay : maximumOrderPerDay,
      rateVAT : rateVAT
    }).subscribe(res => {});

    return this.getAllConstraints();
  }

  deleteConstraint(constraintId)
  {
    this.http.post(this.url + "/deleteConstraint", {constraintId : constraintId})
    .subscribe(res => {});

    return this.getAllConstraints();
  }
}
