import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class ConstraintService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode + "constraints/"; // url souhaitée pour la requête de l'API

  addConstraint(constraint: any): Observable<Object> {
    return this.http.put(this.url + "addConstraint/", {constraint: constraint});
  }
}
