import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Users }from '../interfaces/users'

import { constantes } from '../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class AccueilService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode + "user/"; // url souhaitée pour la requête de l'API

  getUserById(paramsUser: Users): Observable<Object>
  {
    return this.http.get(this.url + "getUserById/"); // Récupère les données renvoyées par l'API
  }

  
}