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
}
