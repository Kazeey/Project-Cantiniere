import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class ManageUserService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPIJAVA + "user/findall"; // url souhaitée pour la requête de l'API

  getAllUsers(): Observable<Object>
  {
    return this.http.get(this.url); // Récupère les données renvoyées par l'API
  }
}
